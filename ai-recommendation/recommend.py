import pandas as pd
import numpy as np
from lightfm import LightFM
from lightfm.data import Dataset
from lightfm.cross_validation import random_train_test_split
from lightfm.evaluation import precision_at_k
from sklearn.feature_extraction.text import TfidfVectorizer
import os

# --- 1. 데이터 로딩 ---
print("="*20)
print("🔍 1. 데이터 로딩 시작")
base_path = os.path.dirname(os.path.abspath(__file__))
interactions_path = os.path.join(base_path, 'trend_recommend_scores.csv')
trends_path = os.path.join(base_path, 'all_trends.csv')

try:
    interactions_df = pd.read_csv(interactions_path)
    trends_df = pd.read_csv(trends_path)
    print(f"  - 'trend_recommend_scores.csv' 로드 완료 (행: {len(interactions_df)}, 열: {len(interactions_df.columns)})")
    print(f"  - 'all_trends.csv' 로드 완료 (행: {len(trends_df)}, 열: {len(trends_df.columns)})")
    if len(interactions_df) == 0 or len(trends_df) == 0:
        print("❌ 오류: 입력 CSV 파일 중 하나가 비어있습니다. 스크립트를 종료합니다.")
        exit()
    # print("\n[interactions_df 샘플]\n", interactions_df.head())
    # print("\n[trends_df 샘플]\n", trends_df.head())
except FileNotFoundError as e:
    print(f"❌ 오류: 파일을 찾을 수 없습니다 - {e}. 스크립트를 종료합니다.")
    exit()

# --- 2. LightFM 데이터셋 구성 ---
print("\n" + "="*20)
print("📊 2. LightFM 데이터셋 구성 시작")
dataset = Dataset()
dataset.fit(
    users=interactions_df['user_id'],
    items=trends_df['trend_id']
)

# 아이템 피처 준비
trends_df['cat_feat'] = 'category:' + trends_df['category'].astype(str)
trends_df['title_feat'] = 'title:' + trends_df['title'].astype(str)
tfidf = TfidfVectorizer(max_features=50)
desc_tfidf = tfidf.fit_transform(trends_df['description'].fillna(""))
all_features = set(trends_df['cat_feat']) | set(trends_df['title_feat']) | set(tfidf.get_feature_names_out())
dataset.fit_partial(items=trends_df['trend_id'], item_features=all_features)

# 상호작용 행렬 생성
(interactions, _) = dataset.build_interactions([
    (row['user_id'], row['trend_id'], row['score']) for _, row in interactions_df.iterrows()
])

num_users, num_items = dataset.interactions_shape()
print(f"  - 총 유저 수: {num_users}")
print(f"  - 총 아이템 수: {num_items}")
print(f"  - 총 상호작용(점수) 수: {interactions.nnz}")

if interactions.nnz == 0:
    print("❌ 오류: 상호작용 데이터가 없습니다. 추천을 생성할 수 없습니다.")
    exit()

# 아이템 피처 행렬 생성
trends_df['features'] = trends_df.apply(
    lambda row: [row['cat_feat'], row['title_feat']] + [
        w for w in tfidf.get_feature_names_out() if pd.notna(row['description']) and w in row['description']
    ],
    axis=1
)
item_features = dataset.build_item_features(
    ((row['trend_id'], row['features']) for _, row in trends_df.iterrows())
)

# --- 3. 학습/테스트 데이터 분할 ---
print("\n" + "="*20)
print("🚂 3. 학습/테스트 데이터 분할 시작")
train, test = random_train_test_split(interactions, test_percentage=0.2, random_state=42)
print(f"  - 학습 데이터 상호작용 수: {train.nnz}")
print(f"  - 테스트 데이터 상호작용 수: {test.nnz}")

# --- 4. 모델 학습 ---
print("\n" + "="*20)
print("🧠 4. 모델 학습 시작")
model = LightFM(loss='warp')
# 윈도우에서는 num_threads가 경고를 발생시킬 수 있으므로, 주석 처리하거나 1로 설정하는 것을 권장
model.fit(train, item_features=item_features, epochs=10, num_threads=1)
print("  - 모델 학습 완료")

# --- 5. 모델 평가 (Precision@k) ---
print("\n" + "="*20)
print("📈 5. 모델 평가 시작")
try:
    if test.nnz == 0:
        print("  - Precision@3: (스킵) 테스트 데이터가 없어 평가할 수 없습니다.")
    else:
        prec = precision_at_k(model, test, item_features=item_features, k=3)
        print(f"  - Precision@3: {prec.mean():.4f}")
except Exception as e:
    print(f"  - Precision@3 계산을 스킵합니다. 이유: {e}")

# --- 6. 추천 생성 ---
print("\n" + "="*20)
print("💡 6. 추천 생성 시작")
user_id_map, _, item_id_map, _ = dataset.mapping()
user_id_reverse = {v: k for k, v in user_id_map.items()}
item_id_reverse = {v: k for k, v in item_id_map.items()}

recommendations = []
num_items = len(item_id_map)
num_users = len(user_id_map)

print(f"  - 추천 대상 유저 수: {num_users}, 아이템 수: {num_items}")

if num_items == 0 or num_users == 0:
    print("  - 추천 불가: 사용자 또는 아이템이 없습니다.")
else:
    for user_inner_id in range(num_users):
        user_external_id = user_id_reverse[user_inner_id]
        scores = model.predict(user_inner_id, np.arange(num_items), item_features=item_features)

        train_csr = train.tocsr()
        known_items = set(train_csr[user_inner_id].indices) if train_csr.shape[0] > user_inner_id else set()

        unseen = [(item, score) for item, score in enumerate(scores) if item not in known_items]

        if not unseen:
            continue

        top_items = sorted(unseen, key=lambda x: -x[1])[:3]
        for item_inner_id, score in top_items:
            trend_id = item_id_reverse[item_inner_id]
            recommendations.append((user_external_id, trend_id, float(score)))

# --- 7. 결과 저장 ---
print("\n" + "="*20)
print("💾 7. 결과 저장 시작")
recommend_df = pd.DataFrame(recommendations, columns=['user_id', 'trend_id', 'score'])
output_path = os.path.join(base_path, 'recommended_trends.csv')
recommend_df.to_csv(output_path, index=False)
print(f"✅ 추천 완료. '{output_path}' 저장됨. (행 수: {len(recommend_df)})")
print("="*20)