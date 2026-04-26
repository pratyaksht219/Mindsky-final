# -------- Stage 1: Base Image --------
FROM python:3.11-slim

# Prevent Python from writing .pyc files
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set working directory
WORKDIR /app

# Install system dependencies (minimal)
RUN apt-get update && apt-get install -y \
    gcc \
    curl \
    && rm -rf /var/lib/apt/lists/*

# -------- Install dependencies --------
COPY requirements.txt .

RUN pip install --upgrade pip setuptools wheel && \
    pip install --no-cache-dir -r requirements.txt

# -------- Copy application --------
COPY . .

# -------- Make start script executable --------
RUN chmod +x start.sh

# -------- Expose port --------
EXPOSE 8000

# -------- Run --------
CMD ["./start.sh"]