#!/bin/bash
# ─────────────────────────────────────────────────────────────────────
# Mindsky Kubernetes Deployment Script (Minikube)
# ─────────────────────────────────────────────────────────────────────
# This script deploys the entire Mindsky platform to a minikube cluster.
#
# Usage:
#   chmod +x deploy.sh
#   ./deploy.sh
#
# Prerequisites:
#   - minikube is installed and running (`minikube start`)
#   - kubectl is installed and configured
#   - The geojson files (india_states.geojson, dists11.geojson) are
#     present in this directory
# ─────────────────────────────────────────────────────────────────────

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "═══════════════════════════════════════════════════════════"
echo "  🧠 Mindsky — Kubernetes Deployment"
echo "═══════════════════════════════════════════════════════════"

# ── Step 1: Namespace + Secrets + ConfigMaps + PVCs ──
echo ""
echo "📦 Step 1/6 — Creating namespace, secrets, configmaps, PVCs..."
kubectl apply -f 00-namespace.yml

if [ ! -f ".env" ]; then
    echo "❌ ERROR: .env file not found in $(pwd)!"
    echo "Please copy .env.example to .env and fill in your API keys before deploying."
    exit 1
fi

echo "🔑 Generating dynamic secrets from .env file..."
kubectl delete secret twilio-secret ai-secret -n mindsky --ignore-not-found
kubectl create secret generic twilio-secret --from-env-file=.env -n mindsky
kubectl create secret generic ai-secret --from-env-file=.env -n mindsky

kubectl apply -f 01-secrets.yml
kubectl apply -f 02-configmaps.yml
kubectl apply -f 03-persistent-volumes.yml

# ── Step 2: Infrastructure (databases + cache) ──
echo ""
echo "🗄️  Step 2/6 — Deploying databases & cache..."
kubectl apply -f 10-redis.yml
kubectl apply -f 11-mongo.yml
kubectl apply -f 12-postgis.yml

echo "   ⏳ Waiting for Redis to be ready..."
kubectl rollout status deployment/redis -n mindsky --timeout=120s

echo "   ⏳ Waiting for MongoDB to be ready..."
kubectl rollout status deployment/mongo -n mindsky --timeout=120s

echo "   ⏳ Waiting for PostGIS to be ready..."
kubectl rollout status deployment/postgis-db -n mindsky --timeout=180s

# ── Step 3: Load GeoData into PostGIS ──
echo ""
echo "🌍 Step 3/6 — Loading GeoJSON data into PostGIS..."

# Apply the geodata PVC + copy-helper pod (not the Job yet)
kubectl apply -f 40-geodata-loader.yml

echo "   ⏳ Waiting for geodata-copy-helper pod to be ready..."
kubectl wait --for=condition=Ready pod/geodata-copy-helper -n mindsky --timeout=120s

echo "   📤 Copying india_states.geojson into the cluster..."
kubectl cp india_states.geojson mindsky/geodata-copy-helper:/data/india_states.geojson

echo "   📤 Copying dists11.geojson into the cluster..."
kubectl cp dists11.geojson mindsky/geodata-copy-helper:/data/dists11.geojson

echo "   ✅ GeoJSON files copied. Cleaning up helper pod..."
kubectl delete pod geodata-copy-helper -n mindsky --ignore-not-found

echo "   🚀 Starting geodata import Job..."
# The Job was already applied, it will now find the files in the PVC
echo "   ⏳ Waiting for geodata-loader Job to complete..."
kubectl wait --for=condition=Complete job/geodata-loader -n mindsky --timeout=600s || {
    echo "   ⚠️  Geodata loader Job may still be running. Check logs with:"
    echo "      kubectl logs job/geodata-loader -n mindsky"
}

# ── Step 4: Core application services ──
echo ""
echo "🔧 Step 4/6 — Deploying core services..."
kubectl apply -f 20-classifier.yml
kubectl apply -f 21-ai.yml
kubectl apply -f 22-screening.yml
kubectl apply -f 23-questionnaire.yml
kubectl apply -f 24-gateway.yml
kubectl apply -f 25-backend.yml
kubectl apply -f 26-frontend.yml

echo "   ⏳ Waiting for services to be ready..."
kubectl rollout status deployment/classifier -n mindsky --timeout=180s
kubectl rollout status deployment/ai -n mindsky --timeout=180s
kubectl rollout status deployment/screening -n mindsky --timeout=180s
kubectl rollout status deployment/questionnaire -n mindsky --timeout=180s
kubectl rollout status deployment/gateway -n mindsky --timeout=180s
kubectl rollout status deployment/backend -n mindsky --timeout=180s
kubectl rollout status deployment/frontend -n mindsky --timeout=120s

# ── Step 5: Emergency services ──
echo ""
echo "🚨 Step 5/6 — Deploying emergency services..."
kubectl apply -f 27-emergency.yml
kubectl apply -f 28-scraper.yml
kubectl apply -f 29-mongo-express.yml

kubectl rollout status deployment/emergency -n mindsky --timeout=180s
kubectl rollout status deployment/scraper-service -n mindsky --timeout=120s

# ── Step 6: Observability stack ──
echo ""
echo "📊 Step 6/6 — Deploying observability stack..."
kubectl apply -f 30-prometheus.yml
kubectl apply -f 31-grafana.yml
kubectl apply -f 32-cadvisor.yml

kubectl rollout status deployment/prometheus -n mindsky --timeout=120s
kubectl rollout status deployment/grafana -n mindsky --timeout=120s

# ── Summary ──
echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  ✅ Mindsky deployed successfully!"
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "  📋 Service Access (via minikube):"
echo "  ─────────────────────────────────"
echo "  Frontend:      minikube service frontend -n mindsky"
echo "  Prometheus:    minikube service prometheus -n mindsky"
echo "  Grafana:       minikube service grafana -n mindsky"
echo "  Mongo Express: minikube service mongo-express -n mindsky"
echo ""
echo "  📋 Useful Commands:"
echo "  ─────────────────────────────────"
echo "  View all pods:     kubectl get pods -n mindsky"
echo "  View all services: kubectl get svc -n mindsky"
echo "  View logs:         kubectl logs -f deployment/<name> -n mindsky"
echo "  Delete everything: kubectl delete namespace mindsky"
echo ""
