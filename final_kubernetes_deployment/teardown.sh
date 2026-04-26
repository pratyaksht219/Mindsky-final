#!/bin/bash
# ─────────────────────────────────────────────────────────────────────
# Mindsky Kubernetes Teardown Script
# ─────────────────────────────────────────────────────────────────────
# Deletes the entire Mindsky namespace and all resources within it.
#
# Usage:
#   chmod +x teardown.sh
#   ./teardown.sh
# ─────────────────────────────────────────────────────────────────────

set -e

echo "═══════════════════════════════════════════════════════════"
echo "  🧹 Mindsky — Kubernetes Teardown"
echo "═══════════════════════════════════════════════════════════"

echo ""
read -p "⚠️  This will delete the entire 'mindsky' namespace. Continue? (y/N) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  Deleting namespace 'mindsky' and all resources..."
    kubectl delete namespace mindsky --ignore-not-found
    echo ""
    echo "✅ Teardown complete."
else
    echo "❌ Cancelled."
fi
