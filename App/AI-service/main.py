from fastapi import FastAPI, HTTPException

from models.AiServiceRequest import AiServiceRequest
from models.AiServiceResponse import AiServiceResponse

# /Users/pratyakshtrivedi/Developer/MindSky-AI/App/AI-service/rag/response_validator.py

from rag.chain import RAGChain
from rag.retriever import RAGRetriever

from langchain.vectorstores import Chroma
from langchain_huggingface import HuggingFaceEmbeddings
from prometheus_fastapi_instrumentator import Instrumentator

from prometheus_client import Counter, Histogram

LLM_LATENCY = Histogram("llm_response_time_seconds", "LLM latency")
VALIDATION_FAILURES = Counter("response_validation_failures_total", "Validation failures")
LLM_FAILURES = Counter("llm_failures_total", "LLM failures")

VECTOR_DB_DIR = "vector_store"
EMBEDDING_MODEL = "sentence-transformers/all-mpnet-base-v2"

embeddings = HuggingFaceEmbeddings(
    model_name=EMBEDDING_MODEL
)
vector_store = Chroma(
    persist_directory=VECTOR_DB_DIR,
    embedding_function=embeddings
)

retriever = RAGRetriever(vector_store)
rag_chain = RAGChain()

app = FastAPI(
    title="Mental Health AI Analysis Service",
    version="1.0.0"
)
Instrumentator().instrument(app).expose(app)
rag_chain = RAGChain()


@app.post("/analyze", response_model=AiServiceResponse)
def analyze(request: AiServiceRequest):
    try:

        print("Recieved the AiServiceRequestDTO from the backend")
        print(request.metadata)
        print(request.assessment)
        print(request.components)
        print(request.riskSignals)
        print(request.constraints)
        print(request.contextHints)

        print("Retrieving context from RAG vector store")
        documents = retriever.retrieve(
            questionnaireId=request.metadata.questionnaireId,
            top_k=6
        )
        print("Document Retrieval Successfull")
        print(documents)

        response = rag_chain.run(
            request=request,
            documents=documents
        )
        return response

        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to analyze assessment: {str(e)}"
        )