from fastapi import FastAPI
from pydantic import BaseModel
from tag_generator import extract_keywords
from trend_recommender import find_similar_trends

app = FastAPI()

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