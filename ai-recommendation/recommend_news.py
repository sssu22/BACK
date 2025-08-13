# recommend_news.py
import requests
import sys
import openai
from openai import OpenAI
import pandas as pd
import os
import urllib.parse
from dotenv import load_dotenv

load_dotenv(dotenv_path="../.env")  # 바깥에 있는 .env 사용

NAVER_CLIENT_ID = os.getenv("NAVER_CLIENT_ID")
NAVER_CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
openai.api_key = OPENAI_API_KEY

client = OpenAI()

# 1. 뉴스 검색
def fetch_news(keyword):
    encoded_keyword = urllib.parse.quote(keyword)
    url = f"https://openapi.naver.com/v1/search/news.json?query={encoded_keyword}&display=3"
    headers = {
        "X-Naver-Client-Id": NAVER_CLIENT_ID,
        "X-Naver-Client-Secret": NAVER_CLIENT_SECRET
    }
    response = requests.get(url, headers=headers)

    items = response.json().get("items", [])
    return [{"title": item["title"], "link": item["link"], "description": item["description"]} for item in items]

# 2. 뉴스 점수화 (GPT)
def score_trend_from_news(news_list):
    content = "다음은 하나의 트렌드에 대한 뉴스 3개입니다.\n\n"
    for i, news in enumerate(news_list, start=1):
        content += f"[뉴스 {i}]\n제목: {news['title']}\n요약: {news['description']}\n\n"

    content += (
        "위 3개의 뉴스 전반에 대해, 해당 트렌드가 현재 시점에서 얼마나 뜨거운 관심을 받고 있는지를 평가해주세요.\n\n"
        "다음 기준에 따라 1~100점으로 평가해주세요:\n"
        "- 최근 뉴스의 양과 다양성 (출처, 시기 등)\n"
        "- 뉴스 제목의 파급력과 대중의 관심도\n"
        "- 요약 내용에서 드러나는 논란성, 중요성 또는 급부상 여부\n\n"
        "예시: 현재 대중의 큰 주목을 받는 트렌드는 80~100점, 과거 이슈거나 관심이 적은 주제는 20~50점 사이가 적절할 수 있습니다.\n\n"
        "숫자만 한 줄로 출력해주세요."
    )

    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": content}]
    )
    return response.choices[0].message.content.strip()

# 3. 실행
def main():
    if len(sys.argv) < 2:
            print("❌ 키워드를 인자로 전달해야 합니다.")
            return
    keyword = sys.argv[1]
    news_list = fetch_news(keyword)
    if not news_list:
            print("❌ 뉴스 없음")
            return
    # 뉴스 3개에 따른 AI 트렌드 점수 측정
    score = score_trend_from_news(news_list)
    results = []

    for news in news_list:
        results.append({
            "keyword": keyword,
            "title": news["title"],
            "link": news["link"],
            "score": score
        })

#     os.makedirs("output", exist_ok=True)
#     safe_keyword = keyword.replace(" ", "_")
#     output_path = f"./output/recommended_news_{safe_keyword}_temp.csv"
#     pd.DataFrame(results).to_csv(output_path, index=False)
#     print(f"✅ 저장 완료: {output_path}")
# 공유 디렉토리 보장
    os.makedirs("/shared", exist_ok=True)

    # 키워드 안전하게 변환
    safe_keyword = keyword.replace(" ", "_")

    # 저장 경로를 /shared로 변경
    output_path = f"/shared/recommended_news_{safe_keyword}_temp.csv"

    # CSV 저장
    pd.DataFrame(results).to_csv(output_path, index=False)

    print(f"✅ 저장 완료: {output_path}")

if __name__ == "__main__":
    main()
