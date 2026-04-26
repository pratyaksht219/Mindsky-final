# -------- Base Image --------
FROM python:3.11-slim

# Prevent Python from writing .pyc files
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set working directory
WORKDIR /app

# -------- Install dependencies --------
COPY requirements.txt .

# 1. Upgrade pip and install python packages
# 2. Install Playwright browser and OS dependencies
RUN apt-get update && \
    pip install --upgrade pip setuptools wheel && \
    pip install --no-cache-dir -r requirements.txt && \
    playwright install chromium && \
    playwright install-deps chromium && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# -------- Copy application --------
COPY . .

# -------- Expose port --------
# Align EXPOSE and the runtime port for Uvicorn
EXPOSE 8001

# -------- Run --------
# Path updated to point to the nested app/main.py component hierarchy
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]