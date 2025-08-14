import os
import numpy as np
# from dotenv import load_dotenv
import pandas as pd
from sqlalchemy import create_engine
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
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

# --- 런타임/스레드 튜닝 (메모리 급증/불안정 방지) ---
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("OPENBLAS_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")
os.environ.setdefault("SENTENCE_TRANSFORMERS_HOME", "/tmp/st_cache")

try:
    import torch
    torch.set_num_threads(1)
except Exception:
    # torch 미설치/에러는 무시 (CPU 1스레드 제한만 못 걸림)
    pass

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

MODEL_ID = os.getenv("SBERT_MODEL", "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
SBERT_BATCH = int(os.getenv("SBERT_BATCH", "8"))          # 인코딩 배치 크기
SIM_MAX_ITEMS = int(os.getenv("SIM_MAX_ITEMS", "2000"))   # 한번에 임베딩할 최대 행 수(피크 메모리 제한)

# 전역 변수
_model = None
def get_model():
    """모델을 처음 호출 시에만 로드 (lazy)"""
    global _model
    if _model is None:
        print("[MODEL] Loading SentenceTransformer:", MODEL_ID)
        _model = SentenceTransformer(MODEL_ID, device="cpu")
    return _model
# def get_model():
#     """모델을 처음 호출 시에만 로드"""
#     global _model
#     if _model is None:
#         print("[MODEL] Loading SentenceTransformer model...")
#         _model = SentenceTransformer('jhgan/ko-sroberta-multitask')
#     return _model

def get_trends():
    """트렌드 목록 최신 조회"""
    query = "SELECT trend_id as id, title, description, category FROM trend"
    df = pd.read_sql(query, engine)
    # 결측치 방어
    df["title"] = df["title"].fillna("").astype(str)
    df["description"] = df["description"].fillna("").astype(str)
    df["category"] = df["category"].fillna("").astype(str)
    return df

# def get_trends():
#     # 트렌드 목록 최신 조회
#     query = "SELECT trend_id as id, title, description, category FROM trend"
#     df = pd.read_sql(query, engine)
#     return df

def find_similar_trends(title: str, description: str, category: str, top_k: int = 3):
    """
    입력 (title+description)과 같은 category의 트렌드들 중 코사인 유사도 상위 top_k 반환.
    - 모델/인코딩 과정 예외가 나도 서비스 전체가 죽지 않도록 빈 리스트 반환.
    - 데이터가 너무 많으면 SIM_MAX_ITEMS로 잘라 피크 메모리 방지.
    """
    try:
        trends_df = get_trends()
        # 카테고리 일치만 비교
        trends_df = trends_df[trends_df["category"] == category].copy()
        if len(trends_df) == 0:
            return []

        # 너무 많으면 상위 N개만 (정렬 기준 없음 → 최근/자주 쓰는 쪽만 원하면 여기에서 정렬 로직 추가 가능)
        if len(trends_df) > SIM_MAX_ITEMS:
            trends_df = trends_df.iloc[:SIM_MAX_ITEMS].copy()

        model = get_model()

        # 입력/코퍼스 텍스트 구성
        input_text = f"{title or ''} {description or ''}".strip()
        corpus_texts = (trends_df["title"] + " " + trends_df["description"]).tolist()

        # 임베딩 (정규화 → 코사인 유사도 안정)
        input_vec = model.encode(
            [input_text],
            convert_to_numpy=True,
            normalize_embeddings=True,
            batch_size=SBERT_BATCH,
            show_progress_bar=False,
        )
        trend_vecs = model.encode(
            corpus_texts,
            convert_to_numpy=True,
            normalize_embeddings=True,
            batch_size=SBERT_BATCH,
            show_progress_bar=False,
        )

        sims = cosine_similarity(input_vec, trend_vecs)[0]
        top_idx = np.argsort(sims)[::-1][:top_k]
        return [int(trends_df.iloc[i]["id"]) for i in top_idx]

    except Exception as e:
        # 어떤 이유로든(메모리, 모델, 인코딩 등) 실패하면 서비스는 살리고 빈 리스트 반환
        print("[SIMILAR] failed:", repr(e))
        return []

# def find_similar_trends(title, description, category, top_k=3):
#     trends_df = get_trends()
#     trends_df = trends_df[trends_df['category'] == category]
#     if len(trends_df) == 0:
#         return []
#
#     model = get_model()
#
#     # 임베딩 준비
#     input_text = f"{title} {description}"
#     input_vec = model.encode([input_text], convert_to_numpy=True)
#
#     trends_df['text'] = trends_df['title'] + ' ' + trends_df['description']
#     trend_vecs = model.encode(trends_df['text'].tolist(), convert_to_numpy=True)
#
#     similarities = cosine_similarity(input_vec, trend_vecs)[0]
#     top_indices = np.argsort(similarities)[::-1][:top_k]
#
#     return [int(trends_df.iloc[idx]["id"]) for idx in top_indices]
#
# #     result = []
# #     for idx in top_indices:
# #         result.append(int(trends_df.iloc[idx]["id"]))
# #     return result

if __name__ == "__main__":
    # 테스트용 입력 (DB에 해당 카테고리의 트렌드가 있는지 확인!)
    title = "카페 레이어드"
    description = "체인점이긴 하지만 케이크가 정말 맛있는 카페 맛집"
    category = "FOOD" 

    result = find_similar_trends(title, description, category)
    print("비슷한 트렌드 결과:")
    for trend in result:
        print(trend)