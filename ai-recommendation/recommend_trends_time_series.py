# recommend_trends_time_series.py

import pandas as pd
from prophet import Prophet
import os

# 현재 파일의 위치를 기준으로 ai-recommendation 디렉토리 안에서 CSV 파일 로드
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CSV_IN  = "/shared/trend_scores.csv"
CSV_OUT = "/shared/predicted_top3.csv"

df = pd.read_csv(CSV_IN)
df["date"] = pd.to_datetime(df["date"])

results = []

for trend_id in df["trend_id"].unique():
    trend_df = df[df["trend_id"] == trend_id][["date", "score"]]
    trend_df = trend_df.rename(columns={"date": "ds", "score": "y"})

    if len(trend_df) < 7:
        continue  # 데이터가 너무 적으면 스킵

    model = Prophet()
    model.fit(trend_df)

    future = model.make_future_dataframe(periods=7)
    forecast = model.predict(future)

    yhat_mean = forecast["yhat"].tail(7).mean()
    prev_mean = trend_df["y"].tail(7).mean()
    increase_rate = (yhat_mean - prev_mean) / prev_mean * 100

    confidence = 100 - (forecast["yhat_upper"] - forecast["yhat_lower"]).tail(7).mean()

    results.append({
        "trend_id": trend_id,
        "increase_rate": round(increase_rate, 2),
        "confidence": round(confidence, 2)
    })

# 결과 저장
result_df = pd.DataFrame(results)
result_df.to_csv(CSV_OUT, index=False)

