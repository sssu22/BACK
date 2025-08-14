import pandas as pd
import numpy as np
from lightfm import LightFM
from lightfm.data import Dataset
from lightfm.cross_validation import random_train_test_split
from lightfm.evaluation import precision_at_k
from sklearn.feature_extraction.text import TfidfVectorizer
from scipy.sparse import hstack
import os

base_path = "/shared"

interactions_path = os.path.join(base_path, 'trend_recommend_scores.csv')
trends_path = os.path.join(base_path, 'all_trends.csv')

interactions_df = pd.read_csv(interactions_path)
trends_df = pd.read_csv(trends_path)

# LightFM Dataset 정의
dataset = Dataset()
dataset.fit(interactions_df['user_id'], trends_df['trend_id'])

# interactions 생성
(interactions, _) = dataset.build_interactions([
    (row['user_id'], row['trend_id'], row['score']) for _, row in interactions_df.iterrows()
])

# 카테고리/제목/설명 feature
trends_df['cat_feat'] = 'category:' + trends_df['category'].astype(str)
trends_df['title_feat'] = 'title:' + trends_df['title'].astype(str)

tfidf = TfidfVectorizer(max_features=50)
desc_tfidf = tfidf.fit_transform(trends_df['description'].fillna(""))

all_features = set(trends_df['cat_feat']) | set(trends_df['title_feat']) | set(tfidf.get_feature_names_out())
dataset.fit_partial(items=trends_df['trend_id'], item_features=all_features)

trends_df['features'] = trends_df.apply(
    lambda row: [row['cat_feat'], row['title_feat']] + [
        w for w in tfidf.get_feature_names_out() if pd.notna(row['description']) and w in row['description']
    ],
    axis=1
)

item_features = dataset.build_item_features(
    ((row['trend_id'], row['features']) for _, row in trends_df.iterrows())
)

# 학습 데이터 분할
train, test = random_train_test_split(interactions, test_percentage=0.2, random_state=42)

# 모델 학습
model = LightFM(loss='warp')
model.fit(train, item_features=item_features, epochs=10, num_threads=2)

# 안전한 precision@k 계산 (유저 1명/테스트 상호작용 0개여도 에러 없이)
try:
    test_csr = test.tocsr()
    mask = np.ravel(test_csr.getnnz(axis=1)) > 0  # 테스트 상호작용이 있는 유저만
    if mask.sum() == 0:
        print("Precision@3: (스킵) 테스트 세트에 유효한 유저가 없습니다.")
    else:
        prec = precision_at_k(
            model, test,
            item_features=item_features,
            k=3,
            preserve_rows=True  # 중요: 원래 행 순서 유지
        )
        prec = np.ravel(prec)
        precision = prec[mask].mean()
        print(f"Precision@3: {precision:.4f}")
except Exception as e:
    # 어떤 환경에서도 평가 때문에 전체 파이프라인이 멈추지 않도록
    print(f"Precision@3 계산을 스킵합니다. 이유: {e}")

# --- 추천 생성 (유저 1명/아이템 전부 이미 봤어도 안전) ---
user_id_map, user_id_reverse = dataset.mapping()[0], {v: k for k, v in dataset.mapping()[0].items()}
item_id_map, item_id_reverse = dataset.mapping()[2], {v: k for k, v in dataset.mapping()[2].items()}

recommendations = []

num_items = len(item_id_map)
num_users = len(user_id_map)

if num_items == 0 or num_users == 0:
    print("추천 불가: 사용자 또는 아이템이 없습니다.")
else:
    for user_inner_id in range(num_users):
        user_external_id = user_id_reverse[user_inner_id]

        # 예측 점수 계산 (아이템이 하나도 없으면 건너뜀)
        scores = model.predict(
            user_inner_id,
            np.arange(num_items),
            item_features=item_features
        )

        # 학습에 사용한 known items 제외
        train_csr = train.tocsr()
        known_items = set(train_csr[user_inner_id].indices) if train_csr.shape[0] > user_inner_id else set()

        # 안 본 아이템만 후보로
        unseen = [(item, score) for item, score in enumerate(scores) if item not in known_items]

        if not unseen:
            # 모든 아이템을 이미 봤거나 상호작용이 없음 → 이 유저에 대한 추천이 비어 있을 수 있음
            continue

        top_items = sorted(unseen, key=lambda x: -x[1])[:3]

        for item_inner_id, score in top_items:
            trend_id = item_id_reverse[item_inner_id]
            recommendations.append((user_external_id, trend_id, float(score)))

# 결과 저장: 추천이 0건이어도 헤더만 있는 CSV가 생성됨
recommend_df = pd.DataFrame(recommendations, columns=['user_id', 'trend_id', 'score'])
output_path = "/shared/recommended_trends.csv"
recommend_df.to_csv(output_path, index=False)
print(f"추천 완료. '{output_path}' 저장됨. (행 수: {len(recommend_df)})")
