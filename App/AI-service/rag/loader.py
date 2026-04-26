from langchain.vectorstores import Chroma
from langchain_huggingface import HuggingFaceEmbeddings

from pathlib import Path

VECTOR_DB_DIR = Path("vector_store")

EMBEDDING_MODEL = "sentence-transformers/all-mpnet-base-v2"

def load_vector_store() -> Chroma:
    """
    Loads the persisted Chroma vector store.
    """

    embeddings = HuggingFaceEmbeddings(
        model_name=EMBEDDING_MODEL
    )

    return Chroma(
        persist_directory=str(VECTOR_DB_DIR),
        embedding_function=embeddings
    )