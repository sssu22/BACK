import os
# from dotenv import load_dotenv
import pandas as pd
from sqlalchemy import create_engine
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
from sqlalchemy.engine import URL

# 환경변수 불러오기
# load_dotenv()
# load_dotenv("/srv/trendlog/.env.prod")  # 절대 경로 지정

# DB_HOST = os.getenv("DB_HOST")
# DB_PORT = os.getenv("DB_PORT")
# DB_USER = os.getenv("DB_USER")
# DB_PASSWORD = os.getenv("DB_PASSWORD")
# DB_NAME = os.getenv("DB_NAME")
# DB_URL = f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
#
# # SQLAlchemy 엔진 준비
# engine = create_engine(DB_URL)
DB_HOST = os.getenv("DB_HOST", "db")
DB_PORT = int(os.getenv("DB_PORT") or 5432)  # None이면 5432
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("DB_NAME", "postgres")

print(f"[DB] host={DB_HOST} port={DB_PORT} db={DB_NAME} user={DB_USER}")

# 2) URL 생성 (특수문자 자동 인코딩)
url = URL.create(
    drivername="postgresql+psycopg2",
    username=DB_USER,
    password=DB_PASSWORD,
    host=DB_HOST,
    port=DB_PORT,
    database=DB_NAME,
)

# 3) 엔진 생성
engine = create_engine(url, pool_pre_ping=True)

# 임베딩 모델 로드(최초 1회만)
# model = SentenceTransformer('jhgan/ko-sroberta-multitask')

# 전역 변수
_model = None

def get_model():
    """모델을 처음 호출 시에만 로드"""
    global _model
    if _model is None:
        print("[MODEL] Loading SentenceTransformer model...")
        _model = SentenceTransformer('jhgan/ko-sroberta-multitask')
    return _model

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

    model = get_model()

    # 임베딩 준비
    input_text = f"{title} {description}"
    input_vec = model.encode([input_text], convert_to_numpy=True)

    trends_df['text'] = trends_df['title'] + ' ' + trends_df['description']
    trend_vecs = model.encode(trends_df['text'].tolist(), convert_to_numpy=True)

    similarities = cosine_similarity(input_vec, trend_vecs)[0]
    top_indices = np.argsort(similarities)[::-1][:top_k]

    return [int(trends_df.iloc[idx]["id"]) for idx in top_indices]

#     result = []
#     for idx in top_indices:
#         result.append(int(trends_df.iloc[idx]["id"]))
#     return result

if __name__ == "__main__":
    # 테스트용 입력 (DB에 해당 카테고리의 트렌드가 있는지 확인!)
    title = "카페 레이어드"
    description = "체인점이긴 하지만 케이크가 정말 맛있는 카페 맛집"
    category = "FOOD" 

    result = find_similar_trends(title, description, category)
    print("비슷한 트렌드 결과:")
    for trend in result:
        print(trend)