from __future__ import annotations

import csv
import os
import shlex
import subprocess
from pathlib import Path
from typing import List, Optional

from fastapi import FastAPI, BackgroundTasks, Body
from pydantic import BaseModel

from tag_generator import extract_keywords
from trend_recommender import find_similar_trends

app = FastAPI(title="Trend Jobs API", version="1.0.0")

BASE_DIR = Path(__file__).resolve().parent           # /app  (ai-recommendation 루트)
PROJ_ROOT = BASE_DIR                                  # 스크립트들이 있는 루트
SHARED_DIR = Path("/shared")                          # docker-compose에서 spring/fastapi 모두 마운트

RECOMMEND_PY = PROJ_ROOT / "recommend.py"             # (일간 트렌드 추천)
FORECAST_PY  = PROJ_ROOT / "recommend_trends_time_series.py"  # (주간 시계열 예측)
NEWS_PY      = PROJ_ROOT / "recommend_news.py"        # (키워드 뉴스 생성)

def run_py(cmd: List[str]) -> int:
    """외부 파이썬 스크립트를 서브프로세스로 실행한다. stdout/stderr는 uvicorn 로그로 출력."""
    print(f"[RUN] {shlex.join(cmd)}  (cwd={PROJ_ROOT})")
    proc = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        cwd=str(PROJ_ROOT),
        env=os.environ.copy(),  # GOOGLE_APPLICATION_CREDENTIALS 등 환경변수 전달
    )
    for line in proc.stdout or []:
        print("[PY]", line.rstrip())
    return proc.wait()

@app.get("/healthz")
def healthz():
    return {"ok": True}



class TagRequest(BaseModel):
    title: str
    description: str

@app.post("/generate-tags")
def generate_tags(req: TagRequest):
    text = f"{req.title} {req.description}"
    tags = extract_keywords(text)
    return {"tags": tags}

class SimilarTrendRequest(BaseModel):
    title: str
    description: str
    category: str

@app.post("/find-similar-trends")
def find_similar(req: SimilarTrendRequest):
    similar_trend_ids = find_similar_trends(req.title, req.description, req.category)
    return {"similar_trend_ids": similar_trend_ids}

def run_trend_recommendations_daily():
    """
    기존 recommend.py 로직 그대로 실행.
    - recommend.py가 CSV/JSON 등을 생성한다면, 경로를 /shared 쪽으로 쓰도록 내부 경로를 맞춰두는 것을 권장.
    """
    code = run_py(["python3", str(RECOMMEND_PY)])
    if code != 0:
        raise RuntimeError(f"recommend.py failed (exit={code})")

@app.post("/jobs/trends/recommend/daily")
def jobs_trends_recommend_daily(background: BackgroundTasks):
    background.add_task(run_trend_recommendations_daily)
    return {"status": "queued", "job": "trends_recommend_daily"}


# --- 2) 트렌드 예측(시계열): 주 1번 (월요일 02:30) ---
def run_time_series_forecast_weekly():
    """
    전제 플로우:
      1) Spring이 /shared/trend_scores.csv 를 먼저 export
      2) 여기서 recommend_trends_time_series.py 실행
      3) 스크립트가 /shared/predicted_top3.csv 를 생성
      4) Spring이 /shared/predicted_top3.csv 를 import (DB 반영)
    """
    code = run_py(["python3", str(FORECAST_PY)])
    if code != 0:
        raise RuntimeError(f"recommend_trends_time_series.py failed (exit={code})")

@app.post("/jobs/trends/forecast/weekly")
def jobs_trends_forecast_weekly(background: BackgroundTasks):
    background.add_task(run_time_series_forecast_weekly)
    return {"status": "queued", "job": "trends_forecast_weekly"}


# --- 3) 뉴스 URL 생성: 트렌드 생성 즉시 & 주간 갱신 ---
class NewsItem(BaseModel):
    keyword: str
    title: str
    link: str
    score: int

class NewsGenerateRequest(BaseModel):
    keyword: str

def generate_news_for_keyword(keyword: str) -> List[NewsItem]:
    """
    recommend_news.py 실행 → 임시 CSV를 /shared에 떨굼 → 읽어서 JSON으로 반환.
    * 권장: recommend_news.py 가 [keyword, output_path] 인자를 지원하도록 약간만 수정.
    * 인자 미지원이라면, recommend_news.py 기본 출력 경로를 그대로 읽어도 된다.
    """
    safe_kw = "_".join(keyword.split())
    tmp_csv = SHARED_DIR / f"recommended_news_{safe_kw}_temp.csv"

    # 1) 스크립트 실행 (keyword, output_path 를 인자로 받도록 구현하는 것을 권장)
    if NEWS_PY.exists():
        cmd = ["python3", str(NEWS_PY), keyword, str(tmp_csv)]
        code = run_py(cmd)
        if code != 0:
            raise RuntimeError(f"recommend_news.py failed (exit={code})")
    else:
        raise RuntimeError("recommend_news.py not found")

    # 2) CSV 읽어서 JSON 변환
    items: List[NewsItem] = []
    if tmp_csv.exists():
        with tmp_csv.open(newline="", encoding="utf-8") as f:
            reader = csv.reader(f)
            _ = next(reader, None)  # header skip
            for row in reader:
                # 기대 포맷: [keyword, title, link, score]
                if len(row) >= 4:
                    try:
                        items.append(NewsItem(
                            keyword=row[0],
                            title=row[1],
                            link=row[2],
                            score=int(row[3]),
                        ))
                    except Exception:
                        continue
        # 임시 파일 정리
        try:
            tmp_csv.unlink(missing_ok=True)
        except Exception:
            pass

    return items


@app.post("/jobs/news/generate", response_model=List[NewsItem])
def jobs_news_generate(body: NewsGenerateRequest = Body(...)):
    """
    트렌드 생성 직후 호출: keyword를 넘기면 뉴스 목록(JSON) 반환.
    Spring은 반환된 JSON으로 RecommendedNews 엔티티를 저장하면 된다.
    """
    return generate_news_for_keyword(body.keyword)


@app.post("/jobs/news/weekly")
def jobs_news_weekly():
    """
    주 1회(월요일 00:00) 갱신용 엔드포인트.
    권장 플로우는 Spring이 모든 트렌드 title을 순회하며 /jobs/news/generate 를 호출하는 것.
    (FastAPI가 DB를 모른다면 여기서 전체를 돌리기 어렵기 때문)
    """
    return {"status": "suggest_call_generate_per_trend_from_spring"}