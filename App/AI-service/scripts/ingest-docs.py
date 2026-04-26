import os
from pathlib import Path

from langchain.document_loaders import PyPDFLoader, TextLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings
from langchain.vectorstores import Chroma

# -----------------------------
# CONFIG
# -----------------------------
BASE_DIR = Path("/app/data/rag_grounding_docs")
VECTOR_DB_DIR = Path("/app/vector_store")
# BASE_DIR = Path("/Users/pratyakshtrivedi/Desktop/Mindsky/App/AI-service/data/rag_grounding_docs")
# VECTOR_DB_DIR = Path("/Users/pratyakshtrivedi/Desktop/Mindsky/App/AI-service/vector_store")

EMBEDDING_MODEL = "sentence-transformers/all-mpnet-base-v2"

# -----------------------------
# Embeddings
# -----------------------------
embeddings = HuggingFaceEmbeddings(
    model_name=EMBEDDING_MODEL
)

# -----------------------------
# Vector DB
# -----------------------------
vector_db = Chroma(
    persist_directory=str(VECTOR_DB_DIR),
    embedding_function=embeddings
)

# -----------------------------
# Chunking strategies
# -----------------------------
SPLITTERS = {
    "guide": RecursiveCharacterTextSplitter(chunk_size=400, chunk_overlap=50), #39 chunks
    "questionnaire": RecursiveCharacterTextSplitter(chunk_size=250, chunk_overlap=30),#226 chunks
    "research": RecursiveCharacterTextSplitter(chunk_size=600, chunk_overlap=80), #1254 chunks
    "book": RecursiveCharacterTextSplitter(chunk_size=900, chunk_overlap=100), #10489 chunks
}

# -----------------------------
# Helper: infer questionnaire id
# -----------------------------
def infer_questionnaire(filename: str) -> str:
    name = filename.lower()
    if "phq" in name:
        return "phq9"
    if "gad" in name:
        return "gad7"
    if "dass" in name:
        return "dass21"
    if "k10" in name:
        return "k10"
    if "pss" in name:
        return "pss10"
    if "asrs" in name:
        return "asrs"
    if "lsas" in name:
        return "lsas"
    if "pcl" in name:
        return "pcl5"
    if "psqi" in name or "sleep" in name:
        return "psqi"
    if "mspss" in name:
        return "mspss"
    return "general"

# -----------------------------
# Ingest function
# -----------------------------
def ingest_folder(
    folder: Path,
    source_type: str,
    priority: int,
    clinical_weight: str
):
    print(f"\n📥 Ingesting {source_type.lower()} from {folder}")

    splitter = SPLITTERS[source_type]

    for file in folder.iterdir():
        if file.is_dir():
            continue

        # If a target filter is provided via sys.argv in main, skip non-matching files
        import sys
        target_filter = sys.argv[1].lower() if len(sys.argv) > 1 else None
        if target_filter and target_filter not in file.name.lower():
            continue

        print(f"  → Processing {file.name}")

        try:
            # Load
            if file.suffix.lower() == ".pdf":
                loader = PyPDFLoader(str(file))
            elif file.suffix.lower() == ".md":
                loader = TextLoader(str(file))
            else:
                print(f"    ⚠️ Skipped unsupported file type")
                continue

            documents = loader.load()

            if not documents:
                print(f"    ⚠️ No documents extracted, skipping")
                continue

            questionnaire = infer_questionnaire(file.name)

            # Attach metadata
            for doc in documents:
                doc.metadata.update({
                    "source_type": source_type,
                    "questionnaire": questionnaire,
                    "document_name": file.name,
                    "priority": priority,
                    "clinical_weight": clinical_weight,
                })

            # Split
            chunks = splitter.split_documents(documents)

            if not chunks:
                print(f"    ⚠️ No chunks produced, skipping")
                continue

            # Deterministic IDs (important for Chroma stability)
            ids = [
                f"{source_type}_{questionnaire}_{file.stem}_{i}"
                for i in range(len(chunks))
            ]

            # Store
            vector_db.add_documents(chunks, ids=ids)

            print(f"    ✅ Stored {len(chunks)} chunks")
        except Exception as e:
            print(f"    ❌ ERROR processing {file.name}: {str(e)}")
            continue

# -----------------------------
# MAIN
# -----------------------------
def main():
    import sys
    target_filter = sys.argv[1].lower() if len(sys.argv) > 1 else None

    if target_filter:
        print(f"🔍 Running TARGETED ingest for files containing: '{target_filter}'")

    def should_ingest(folder: Path):
        if not target_filter: return True
        return any(target_filter in f.name.lower() for f in folder.iterdir())

    if should_ingest(BASE_DIR / "Guides"):
        ingest_folder(BASE_DIR / "Guides", "guide", 1, "high")

    if should_ingest(BASE_DIR / "Questionnaire"):
        ingest_folder(BASE_DIR / "Questionnaire", "questionnaire", 2, "high")

    if should_ingest(BASE_DIR / "Questionnaire_Official_Research_References"):
        ingest_folder(BASE_DIR / "Questionnaire_Official_Research_References", "research", 3, "high")

    if (BASE_DIR / "finalized book references").exists() and should_ingest(BASE_DIR / "finalized book references"):
        ingest_folder(BASE_DIR / "finalized book references", "book", 4, "medium")

    try:
        vector_db.persist()
    except Exception:
        pass
    print("\n✅ Vector database ingestion complete")

if __name__ == "__main__":
    main()