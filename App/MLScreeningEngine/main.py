from fastapi import FastAPI
from pydantic import BaseModel
from classifier import classify
# /Users/pratyakshtrivedi/Developer/MindSky-AI/App/MLScreeningEngine/main.py
from prometheus_fastapi_instrumentator import Instrumentator
from prometheus_client import Counter, Histogram

LLM_LATENCY = Histogram("llm_response_time_seconds", "LLM latency")
VALIDATION_FAILURES = Counter("response_validation_failures_total", "Validation failures")
LLM_FAILURES = Counter("llm_failures_total", "LLM failures")

app = FastAPI()

Instrumentator().instrument(app).expose(app)

class Request(BaseModel):
    text: str


@app.post("/classify")
def classify_message(req: Request):

    scores = classify(req.text)

    return {
        "scores": scores
    }