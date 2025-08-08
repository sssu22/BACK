import os
from dotenv import load_dotenv
import pandas as pd
from sqlalchemy import create_engine
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

# 환경변수 불러오기
load_dotenv()
DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_NAME = os.getenv("DB_NAME")

DB_URL = f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

# SQLAlchemy 엔진 준비
engine = create_engine(DB_URL)

# 임베딩 모델 로드(최초 1회만)
model = SentenceTransformer('jhgan/ko-sroberta-multitask')

def get_trends():
    # 트렌드 목록 최신 조회
    query = "SELECT trend_id as id, title, description, category FROM trend"
    df = pd.read_sql(query, engine)
    return df

def find_similar_trends(title, description, category, top_k=3):
    trends_df = get_trends()
    trends_df = trends_df[trends_df['category'] == category]
    if len(trends_df) == 0:
        return []

    # 임베딩 준비
    input_text = f"{title} {description}"
    input_vec = model.encode([input_text], convert_to_numpy=True)

    trends_df['text'] = trends_df['title'] + ' ' + trends_df['description']
    trend_vecs = model.encode(trends_df['text'].tolist(), convert_to_numpy=True)

    similarities = cosine_similarity(input_vec, trend_vecs)[0]
    top_indices = np.argsort(similarities)[::-1][:top_k]
    
    result = []
    for idx in top_indices:
        result.append(int(trends_df.iloc[idx]["id"]))
    return result

if __name__ == "__main__":
    # 테스트용 입력 (DB에 해당 카테고리의 트렌드가 있는지 확인!)
    title = "카페 레이어드"
    description = "체인점이긴 하지만 케이크가 정말 맛있는 카페 맛집"
    category = "FOOD" 

    result = find_similar_trends(title, description, category)
    print("비슷한 트렌드 결과:")
    for trend in result:
        print(trend)