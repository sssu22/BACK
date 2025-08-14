# import os
# import numpy as np
# # from dotenv import load_dotenv
# import pandas as pd
# from sqlalchemy import create_engine
# from sentence_transformers import SentenceTransformer
# from sklearn.metrics.pairwise import cosine_similarity
# from sqlalchemy.engine import URL
#
# # 환경변수 불러오기
# # load_dotenv()
# # load_dotenv("/srv/trendlog/.env.prod")  # 절대 경로 지정
#
# # DB_HOST = os.getenv("DB_HOST")
# # DB_PORT = os.getenv("DB_PORT")
# # DB_USER = os.getenv("DB_USER")
# # DB_PASSWORD = os.getenv("DB_PASSWORD")
# # DB_NAME = os.getenv("DB_NAME")
# # DB_URL = f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
# #
# # # SQLAlchemy 엔진 준비
# # engine = create_engine(DB_URL)
#
# # --- 런타임/스레드 튜닝 (메모리 급증/불안정 방지) ---
# os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
# os.environ.setdefault("OMP_NUM_THREADS", "1")
# os.environ.setdefault("OPENBLAS_NUM_THREADS", "1")
# os.environ.setdefault("MKL_NUM_THREADS", "1")
# os.environ.setdefault("SENTENCE_TRANSFORMERS_HOME", "/tmp/st_cache")
#
# try:
#     import torch
#     torch.set_num_threads(1)
# except Exception:
#     # torch 미설치/에러는 무시 (CPU 1스레드 제한만 못 걸림)
#     pass
#
# DB_HOST = os.getenv("DB_HOST", "db")
# DB_PORT = int(os.getenv("DB_PORT") or 5432)  # None이면 5432
# DB_USER = os.getenv("DB_USER", "postgres")
# DB_PASSWORD = os.getenv("DB_PASSWORD", "")
# DB_NAME = os.getenv("DB_NAME", "postgres")
#
# print(f"[DB] host={DB_HOST} port={DB_PORT} db={DB_NAME} user={DB_USER}")
#
# # 2) URL 생성 (특수문자 자동 인코딩)
# url = URL.create(
#     drivername="postgresql+psycopg2",
#     username=DB_USER,
#     password=DB_PASSWORD,
#     host=DB_HOST,
#     port=DB_PORT,
#     database=DB_NAME,
# )
#
# # 3) 엔진 생성
# engine = create_engine(url, pool_pre_ping=True)
#
# # 임베딩 모델 로드(최초 1회만)
# # model = SentenceTransformer('jhgan/ko-sroberta-multitask')
#
# MODEL_ID = os.getenv("SBERT_MODEL", "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
# SBERT_BATCH = int(os.getenv("SBERT_BATCH", "8"))          # 인코딩 배치 크기
# SIM_MAX_ITEMS = int(os.getenv("SIM_MAX_ITEMS", "2000"))   # 한번에 임베딩할 최대 행 수(피크 메모리 제한)
#
# # 전역 변수
# _model = None
# def get_model():
#     """모델을 처음 호출 시에만 로드 (lazy)"""
#     global _model
#     if _model is None:
#         print("[MODEL] Loading SentenceTransformer:", MODEL_ID)
#         _model = SentenceTransformer(MODEL_ID, device="cpu")
#     return _model
# # def get_model():
# #     """모델을 처음 호출 시에만 로드"""
# #     global _model
# #     if _model is None:
# #         print("[MODEL] Loading SentenceTransformer model...")
# #         _model = SentenceTransformer('jhgan/ko-sroberta-multitask')
# #     return _model
#
# def get_trends():
#     """트렌드 목록 최신 조회"""
#     query = "SELECT trend_id as id, title, description, category FROM trend"
#     df = pd.read_sql(query, engine)
#     # 결측치 방어
#     df["title"] = df["title"].fillna("").astype(str)
#     df["description"] = df["description"].fillna("").astype(str)
#     df["category"] = df["category"].fillna("").astype(str)
#     return df
#
# # def get_trends():
# #     # 트렌드 목록 최신 조회
# #     query = "SELECT trend_id as id, title, description, category FROM trend"
# #     df = pd.read_sql(query, engine)
# #     return df
#
# def find_similar_trends(title: str, description: str, category: str, top_k: int = 3):
#     """
#     입력 (title+description)과 같은 category의 트렌드들 중 코사인 유사도 상위 top_k 반환.
#     - 모델/인코딩 과정 예외가 나도 서비스 전체가 죽지 않도록 빈 리스트 반환.
#     - 데이터가 너무 많으면 SIM_MAX_ITEMS로 잘라 피크 메모리 방지.
#     """
#     try:
#         trends_df = get_trends()
#         # 카테고리 일치만 비교
#         trends_df = trends_df[trends_df["category"] == category].copy()
#         if len(trends_df) == 0:
#             return []
#
#         # 너무 많으면 상위 N개만 (정렬 기준 없음 → 최근/자주 쓰는 쪽만 원하면 여기에서 정렬 로직 추가 가능)
#         if len(trends_df) > SIM_MAX_ITEMS:
#             trends_df = trends_df.iloc[:SIM_MAX_ITEMS].copy()
#
#         model = get_model()
#
#         # 입력/코퍼스 텍스트 구성
#         input_text = f"{title or ''} {description or ''}".strip()
#         corpus_texts = (trends_df["title"] + " " + trends_df["description"]).tolist()
#
#         # 임베딩 (정규화 → 코사인 유사도 안정)
#         input_vec = model.encode(
#             [input_text],
#             convert_to_numpy=True,
#             normalize_embeddings=True,
#             batch_size=SBERT_BATCH,
#             show_progress_bar=False,
#         )
#         trend_vecs = model.encode(
#             corpus_texts,
#             convert_to_numpy=True,
#             normalize_embeddings=True,
#             batch_size=SBERT_BATCH,
#             show_progress_bar=False,
#         )
#
#         sims = cosine_similarity(input_vec, trend_vecs)[0]
#         top_idx = np.argsort(sims)[::-1][:top_k]
#         return [int(trends_df.iloc[i]["id"]) for i in top_idx]
#
#     except Exception as e:
#         # 어떤 이유로든(메모리, 모델, 인코딩 등) 실패하면 서비스는 살리고 빈 리스트 반환
#         print("[SIMILAR] failed:", repr(e))
#         return []
#
# # def find_similar_trends(title, description, category, top_k=3):
# #     trends_df = get_trends()
# #     trends_df = trends_df[trends_df['category'] == category]
# #     if len(trends_df) == 0:
# #         return []
# #
# #     model = get_model()
# #
# #     # 임베딩 준비
# #     input_text = f"{title} {description}"
# #     input_vec = model.encode([input_text], convert_to_numpy=True)
# #
# #     trends_df['text'] = trends_df['title'] + ' ' + trends_df['description']
# #     trend_vecs = model.encode(trends_df['text'].tolist(), convert_to_numpy=True)
# #
# #     similarities = cosine_similarity(input_vec, trend_vecs)[0]
# #     top_indices = np.argsort(similarities)[::-1][:top_k]
# #
# #     return [int(trends_df.iloc[idx]["id"]) for idx in top_indices]
# #
# # #     result = []
# # #     for idx in top_indices:
# # #         result.append(int(trends_df.iloc[idx]["id"]))
# # #     return result
#
# if __name__ == "__main__":
#     # 테스트용 입력 (DB에 해당 카테고리의 트렌드가 있는지 확인!)
#     title = "카페 레이어드"
#     description = "체인점이긴 하지만 케이크가 정말 맛있는 카페 맛집"
#     category = "FOOD"
#
#     result = find_similar_trends(title, description, category)
#     print("비슷한 트렌드 결과:")
#     for trend in result:
#         print(trend)

# trend_recommender.py (ultra-light ver.)
import os
import gc
import numpy as np
import pandas as pd
from sqlalchemy import create_engine, text
from sqlalchemy.engine import URL

# ---- 런타임/스레드 튜닝 ----
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("OPENBLAS_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")
os.environ.setdefault("SENTENCE_TRANSFORMERS_HOME", "/tmp/st_cache")

try:
    import torch
    torch.set_num_threads(1)
except Exception:
    pass

# ---- DB ----
DB_HOST = os.getenv("DB_HOST", "db")
DB_PORT = int(os.getenv("DB_PORT") or 5432)
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("DB_NAME", "postgres")

print(f"[DB] host={DB_HOST} port={DB_PORT} db={DB_NAME} user={DB_USER}")

url = URL.create(
    drivername="postgresql+psycopg2",
    username=DB_USER,
    password=DB_PASSWORD,
    host=DB_HOST,
    port=DB_PORT,
    database=DB_NAME,
)
engine = create_engine(url, pool_pre_ping=True)

# ---- 모델 설정 ----
from sentence_transformers import SentenceTransformer

MODEL_ID = os.getenv("EMBED_MODEL", "intfloat/multilingual-e5-small")  # 더 작고 빠른 기본값
SBERT_BATCH = int(os.getenv("SBERT_BATCH", "8"))
SBERT_MAX_SEQ_LEN = int(os.getenv("SBERT_MAX_SEQ_LEN", "128"))         # 문장 길이 축소로 속도/메모리↓
DB_CHUNK_SIZE = int(os.getenv("DB_CHUNK_SIZE", "500"))                  # DB에서 청크 크기
SIM_MAX_ITEMS = int(os.getenv("SIM_MAX_ITEMS", "20000"))                # 카테고리 내 최대 건수(안전빵 상한)
EMB_DTYPE = os.getenv("EMB_DTYPE", "float16")                           # 임베딩 dtype: float32/float16

# 전역 모델 (lazy)
_model = None
def get_model():
    global _model
    if _model is None:
        print(f"[MODEL] Loading SentenceTransformer: {MODEL_ID}")
        _model = SentenceTransformer(MODEL_ID, device="cpu")
        # 길이 제한으로 속도/메모리 절약
        try:
            _model.max_seq_length = SBERT_MAX_SEQ_LEN
        except Exception:
            pass
    return _model

# ---- 유틸: 코사인 유사도(정규화된 벡터 기준 → 내적 = cos) ----
def normalize_rows(x: np.ndarray) -> np.ndarray:
    # x: (N, D)
    denom = np.linalg.norm(x, axis=1, keepdims=True) + 1e-12
    return x / denom

def cosine_sim_to_matrix(q: np.ndarray, M: np.ndarray) -> np.ndarray:
    # q: (D,) 혹은 (1, D), M: (N, D), 둘 다 정규화되어 있다고 가정
    if q.ndim == 2:
        q = q[0]
    return M @ q  # (N,)

# ---- DB iterator: 카테고리 WHERE로 선필터링 + 청크 읽기 ----
def iter_trends_by_category(category: str, limit: int | None = SIM_MAX_ITEMS, chunk_size: int = DB_CHUNK_SIZE):
    sql = text("""
        SELECT trend_id AS id, title, description
        FROM trend
        WHERE category = :cat
        ORDER BY trend_id
        LIMIT :lim
    """)
    with engine.begin() as conn:
        # pandas read_sql_query with chunksize → 제너레이터
        for chunk in pd.read_sql_query(
            sql, conn, params={"cat": category, "lim": limit}, chunksize=chunk_size
        ):
            # 결측 방어 + 문자열화
            chunk["title"] = chunk["title"].fillna("").astype(str)
            chunk["description"] = chunk["description"].fillna("").astype(str)
            yield chunk

def find_similar_trends(title: str, description: str, category: str, top_k: int = 3):
    try:
        model = get_model()

        # 1) 입력 임베딩 (정규화)
        input_text = (f"{title or ''} {description or ''}").strip()
        q = model.encode([input_text], convert_to_numpy=True, batch_size=SBERT_BATCH, show_progress_bar=False)
        q = normalize_rows(q.astype(np.float32))  # 쿼리는 float32 유지(안정적)

        # 2) DB를 카테고리로 선필터링하며 청크 처리
        best_ids: list[int] = []
        best_sims: list[float] = []

        for df in iter_trends_by_category(category):
            texts = (df["title"] + " " + df["description"]).tolist()
            # 3) 청크 임베딩
            M = model.encode(
                texts,
                convert_to_numpy=True,
                batch_size=SBERT_BATCH,
                show_progress_bar=False,
            ).astype(np.float32)               # encode 결과는 보통 float32
            M = normalize_rows(M)              # 정규화
            if EMB_DTYPE == "float16":
                # 메모리 더 아끼고 싶으면 half로 캐스팅 (정밀도 소폭 손실 가능)
                M = M.astype(np.float16)
                q_local = q.astype(np.float16)
            else:
                q_local = q

            # 4) 유사도 계산(코사인=내적)
            sims = cosine_sim_to_matrix(q_local[0], M)  # (N,)

            # 5) 현재 청크 top_k만 뽑아 전역 best와 병합(메모리 절약)
            if len(sims) > top_k:
                idx_local = np.argpartition(sims, -top_k)[-top_k:]
            else:
                idx_local = np.arange(len(sims))

            # 후보 병합
            for i in idx_local:
                s = float(sims[i])
                id_ = int(df.iloc[i]["id"])
                if len(best_ids) < top_k:
                    best_ids.append(id_)
                    best_sims.append(s)
                else:
                    # 현재 최솟값보다 크면 교체
                    j_min = int(np.argmin(best_sims))
                    if s > best_sims[j_min]:
                        best_sims[j_min] = s
                        best_ids[j_min] = id_

            # 청크 메모리 즉시 해제
            del M, sims, df, texts
            gc.collect()

        # 6) 최종 정렬 후 id 반환
        if not best_ids:
            return []
        order = np.argsort(best_sims)[::-1]
        return [int(best_ids[i]) for i in order[:top_k]]

    except Exception as e:
        print("[SIMILAR] failed:", repr(e))
        return []

if __name__ == "__main__":
    title = "카페 레이어드"
    description = "체인점이긴 하지만 케이크가 정말 맛있는 카페 맛집"
    category = "FOOD"
    print(find_similar_trends(title, description, category))
