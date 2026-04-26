# Mindsky - Mental Health Assessment & Crisis Platform

Mindsky is a comprehensive, microservices-based platform designed to provide automated psychological screening, structured assessments, AI-driven insights, and emergency crisis response. The system uses a modern stack combining Node.js, Spring Boot, and FastAPI, fully containerized and orchestrated via Kubernetes.

---

## 🏗️ Architecture & Microservices

The application is broken down into highly specialized microservices:

### Core Services
1. **Frontend (React/Vite & Nginx)**
   - **Port**: `5173` (Local/Docker)
   - **Features**: User dashboard, gamification (XP, levels), journaling, mood tracking, AI chatbot interface, and emergency panic button.
2. **Backend (Node.js/Express & MongoDB)**
   - **Port**: `5001`
   - **Features**: Handles user authentication, profile gamification, journal entries, and routes complex requests (AI, Crisis) to the appropriate microservice.

### Assessment & AI Services
3. **Gateway Service (Spring Boot)**
   - **Port**: `8080`
   - **Features**: API Gateway orchestrating the conversational flows between the screening engines and questionnaire services.
4. **Screening Service (Spring Boot)**
   - **Port**: `8081`
   - **Features**: Manages the initial psychological screening flow, interacting with the ML Classifier. Uses Redis for caching.
5. **Questionnaire Service (Spring Boot)**
   - **Port**: `8082`
   - **Features**: Manages structured clinical assessments (e.g., GAD-7) using an H2 database.
6. **ML Screening Engine (FastAPI)**
   - **Port**: `9000`
   - **Features**: Uses `sentence-transformers` to perform cosine-similarity prototype matching on user text to classify psychological domains.
7. **AI Service (FastAPI)**
   - **Port**: `8000`
   - **Features**: A Retrieval-Augmented Generation (RAG) pipeline using Chroma DB, Langchain, and LLMs (Groq/OpenRouter) to provide deeply contextualized psychological recommendations and insights based on the assessment results.

### Emergency & Support Services
8. **Emergency Service (Spring Boot & PostGIS)**
   - **Port**: `8084`
   - **Features**: Twilio-powered emergency SMS and Voice Call dispatcher. Uses PostGIS to perform reverse geo-tagging (mapping user coordinates to specific Indian districts/states) to route help effectively.
9. **Google Maps Scraper (FastAPI)**
   - **Port**: `8001`
   - **Features**: Background worker to dynamically scrape nearby psychologists and psychiatrists based on geographic grids.

### Observability Stack
- **Prometheus** (`9090`): Scrapes metrics from Spring Boot actuators, FastAPI instrumentators, and Express middleware.
- **Grafana** (`3000`): Visualizes API latency, LLM response times, and failure rates.
- **cAdvisor** (`8088`): Monitors Docker container resource usage.

---

## ⚙️ Environment Variables Setup

Before running the application, you must configure the environment variables. 
Navigate to `MindSky_deployment` and copy `.env.example` to `.env`:

```bash
cd MindSky_deployment
cp .env.example .env
```

Ensure the following critical keys are filled in without spaces around the equals sign:
```env
# Twilio (For Crisis Protocol)
TWILIO_SID=your_sid_here
TWILIO_AUTH_TOKEN=your_token_here
TWILIO_NUMBER=+1234567890

# AI LLM Keys
GROQ_API_KEY=your_groq_key
OPENROUTER_API_KEY=your_openrouter_key

# URLs will be pre-filled to point to the docker-compose internal network
```

---

## 🐳 Local Deployment (Docker Compose)

The easiest way to spin up the entire system locally is using Docker Compose.

1. Ensure Docker daemon is running.
2. Navigate to the deployment directory:
   ```bash
   cd MindSky_deployment
   ```
3. Start the entire cluster in the background:
   ```bash
   docker-compose up
   ```
4. **Access the Application**:
   - Web App: [http://localhost:5173](http://localhost:5173)
   - Grafana Dashboards: [http://localhost:3000](http://localhost:3000)
   - Mongo Express UI: [http://localhost:8002](http://localhost:8002)

*(Note: On the first run, the PostGIS `data-loader` container will take about 1-2 minutes to ingest the Indian State & District GeoJSON mapping files into the database. The Emergency service will wait for this to finish).*

---

## ☸️ Production Deployment (Kubernetes)

The project is fully ready for Kubernetes orchestration (e.g., using Minikube).

### Prerequisites
- `minikube` installed and started (`minikube start`)
- `kubectl` configured
- Ensure `india_states.geojson` and `dists11.geojson` are present in `final_kubernetes_deployment`.

### Deployment Steps
1. Navigate to the Kubernetes deployment directory:
   ```bash
   cd final_kubernetes_deployment
   ```
2. Set up your Kubernetes environment variables:
   ```bash
   cp .env.example .env
   ```
   *(Ensure you fill in your Twilio and AI API keys in this new `.env` file. The `deploy.sh` script will automatically map them into secure Kubernetes Secrets).*
3. Make the script executable and run it:
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```
3. **What the script does**:
   - Creates the `mindsky` namespace, ConfigMaps, Secrets, and Persistent Volumes.
   - Deploys infrastructure (Redis, Mongo, PostGIS).
   - Spins up a temporary job to load the GeoJSON spatial data into PostGIS.
   - Deploys all Core, AI, and Emergency Microservices.
   - Stands up the Prometheus and Grafana observability stack.
4. **Accessing the Cluster**:
   Because Kubernetes assigns dynamic node ports in Minikube, run the following to access the frontend:
   ```bash
   minikube service frontend -n mindsky
   ```

### Teardown
To cleanly remove the entire deployment from Kubernetes:
```bash
./teardown.sh
```

---
**Maintained by**: Pratyaksh Trivedi
