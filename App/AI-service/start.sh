#!/bin/bash
set -e  # 🔥 exit if any command fails

echo "Checking vector database..."

if [ -z "$(ls -A /app/vector_store 2>/dev/null)" ]; then
  echo "Vector DB empty. Running ingestion..."
  python scripts/ingest-docs.py
else
  echo "Vector DB already exists."
fi

echo "Starting AI service..."
exec uvicorn main:app --host 0.0.0.0 --port 8000