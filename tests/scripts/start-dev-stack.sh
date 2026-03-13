#!/usr/bin/env bash
set -euo pipefail

cd /workspaces/ai-powered-dev-sandbox/product/backend
gradle bootRun >/tmp/backend-e2e.log 2>&1 &
backend_pid=$!

cleanup() {
  kill "$backend_pid" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

for _ in $(seq 1 120); do
  if curl -fsS http://127.0.0.1:8080/api/greeting >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

curl -fsS http://127.0.0.1:8080/api/greeting >/dev/null

cd /workspaces/ai-powered-dev-sandbox/product/frontend
rm -rf .next
BACKEND_BASE_URL=http://127.0.0.1:8080 npm run dev
