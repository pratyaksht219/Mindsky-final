from pathlib import Path
from langchain_huggingface import HuggingFaceEmbeddings
from langchain.vectorstores import Chroma

EMBEDDING_MODEL = "sentence-transformers/all-mpnet-base-v2"
VECTOR_DB_DIR = Path("/Users/pratyakshtrivedi/Developer/Capstone/App/AI-service/vector_store")

# Initialize embeddings
embeddings = HuggingFaceEmbeddings(
    model_name=EMBEDDING_MODEL,
    model_kwargs={"device": "cpu"},  # change to "cuda" if GPU
    encode_kwargs={"normalize_embeddings": True}
)

# Load vector DB
vector_db = Chroma(
    persist_directory=str(VECTOR_DB_DIR),
    embedding_function=embeddings
)

# Query
results = vector_db.similarity_search(
    "What does a high PHQ-9 score mean?",
    k=3
)

# Print results
for r in results:
    print(r.metadata)

# Retrieval result from the vector store :

# [
#   {
#     "clinical_weight": "high", 
#     "document_name": "phq9.md", 
#     "priority": 1, "questionnaire": 
#     "phq9", 
#     "source": "/Users/pratyakshtrivedi/Developer/Capstone/App/AI-service/data/rag_grounding_docs/Guides/phq9.md", 
#     "source_type": "guide"
#   },
#   {
#     "clinical_weight": "high", 
#     "document_name": "phq9.md", 
#     "priority": 1, 
#     "questionnaire": "phq9", 
#     "source": "/Users/pratyakshtrivedi/Developer/Capstone/App/AI-service/data/rag_grounding_docs/Guides/phq9.md", 
#     "source_type": "guide"
#   },
#   {
#     "clinical_weight": "high",
#     "document_name": "PSS-10.pdf",
#     "page": 1,
#     "priority": 2, 
#     "questionnaire": "pss10", 
#     "source": "/Users/pratyakshtrivedi/Developer/Capstone/App/AI-service/data/rag_grounding_docs/Questionnaire/PSS-10.pdf", 
#     "source_type": "questionnaire"}
# ]