import pandas as pd
import numpy as np
from lightfm import LightFM
from lightfm.data import Dataset
from lightfm.cross_validation import random_train_test_split
from lightfm.evaluation import precision_at_k
from sklearn.feature_extraction.text import TfidfVectorizer
from scipy.sparse import hstack
import os

base_path = os.path.dirname(os.path.abspath(__file__))

# interactions_df = pd.read_csv('trend_recommend_scores.csv')
# trends_df = pd.read_csv('all_trends.csv')
interactions_path = os.path.join(base_path, 'trend_recommend_scores.csv')
trends_path = os.path.join(base_path, 'all_trends.csv')

interactions_df = pd.read_csv(interactions_path)
trends_df = pd.read_csv(trends_path)

# LightFM Dataset 정의
dataset = Dataset()
dataset.fit(interactions_df['user_id'], trends_df['trend_id'])

# 기본 interactions 생성
(interactions, _) = dataset.build_interactions([
    (row['user_id'], row['trend_id'], row['score']) for _, row in interactions_df.iterrows()
])

# 카테고리/제목/설명을 feature로 생성
trends_df['cat_feat'] = 'category:' + trends_df['category']
trends_df['title_feat'] = 'title:' + trends_df['title']

tfidf = TfidfVectorizer(max_features=50)
desc_tfidf = tfidf.fit_transform(trends_df['description'].fillna(""))

# 전체 feature 집합
all_features = set(trends_df['cat_feat']) | set(trends_df['title_feat']) | set(tfidf.get_feature_names_out())
dataset.fit_partial(items=trends_df['trend_id'], item_features=all_features)

trends_df['features'] = trends_df.apply(
    lambda row: [row['cat_feat'], row['title_feat']] + [
        word for word in tfidf.get_feature_names_out() if word in row['description']
    ],
    axis=1
)

item_features = dataset.build_item_features(
    ((row['trend_id'], row['features']) for _, row in trends_df.iterrows())
)

# 학습 데이터 분할 및 평가용 모델 학습
train, test = random_train_test_split(interactions, test_percentage=0.2)

model = LightFM(loss='warp')
model.fit(train, item_features=item_features, epochs=10, num_threads=2)

# precision@3 평가 출력->일단 데이터가 적어서 적확성을 측정하기 위해 넣음
precision = precision_at_k(model, test, item_features=item_features, k=3).mean()
print(f"Precision@3: {precision:.4f}")

# 추천 결과 생성
user_id_map, user_id_reverse = dataset.mapping()[0], {v: k for k, v in dataset.mapping()[0].items()}
item_id_map, item_id_reverse = dataset.mapping()[2], {v: k for k, v in dataset.mapping()[2].items()}

recommendations = []

for user_inner_id in range(len(user_id_map)):
    user_external_id = user_id_reverse[user_inner_id]
    scores = model.predict(user_inner_id, np.arange(len(item_id_map)), item_features=item_features)
    known_items = set(train.tocsr()[user_inner_id].indices)

    unseen_items_scores = [(item, score) for item, score in enumerate(scores) if item not in known_items]
    top_items = sorted(unseen_items_scores, key=lambda x: -x[1])[:3]

    for item_inner_id, score in top_items:
        trend_id = item_id_reverse[item_inner_id]
        recommendations.append((user_external_id, trend_id, float(score)))

# 추천 결과 저장
recommend_df = pd.DataFrame(recommendations, columns=['user_id', 'trend_id', 'score'])
output_path = os.path.join(base_path, 'recommended_trends.csv')

recommend_df.to_csv(output_path, index=False)
print("추천 완료. 'recommended_trends.csv' 저장됨.")

