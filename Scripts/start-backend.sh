#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v mvn &>/dev/null; then
  echo "[ERROR] Maven not found."
  exit 1
fi

MVN="mvn"
LOG_DIR="${ROOT_DIR}/logs"
mkdir -p "$LOG_DIR"
PIDS=""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
RED='\033[0;31m'
NC='\033[0m'
BOLD='\033[1m'

cleanup() {
  echo -e "\n${CYAN}${BOLD}▲ ShopFlux Backend Teardown${NC}"
  echo -e "${GRAY}│${NC}"
  echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Stopping Microservices${NC}"
  echo -e "${GRAY}│  ├─${NC} Terminating Spring Boot processes..."
  for pid in $PIDS; do
    kill "$pid" 2>/dev/null || true
  done
  wait 2>/dev/null || true
  echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Services terminated${NC}"
  echo -e "${GRAY}│${NC}"
  echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Backend gracefully stopped${NC}\n"
}
trap cleanup EXIT INT TERM

# --- Service Config ---

service_pom() {
  case "$1" in
    gateway)      echo "Gateway/pom.xml" ;;
    auth)         echo "Backend/auth-service/pom.xml" ;;
    catalog)      echo "Backend/catalog-service/pom.xml" ;;
    search)       echo "Backend/search-service/pom.xml" ;;
    cart)         echo "Backend/cart-service/pom.xml" ;;
    pricing)      echo "Backend/pricing-service/pom.xml" ;;
    inventory)    echo "Backend/inventory-service/pom.xml" ;;
    order)        echo "Backend/order-service/pom.xml" ;;
    payment)      echo "Backend/payment-service/pom.xml" ;;
    notification) echo "Backend/notification-service/pom.xml" ;;
    analytics)    echo "Backend/analytics-service/pom.xml" ;;
    *) echo "" ;;
  esac
}

service_port() {
  case "$1" in
    gateway) echo 8080 ;;
    auth) echo 8081 ;;
    catalog) echo 8082 ;;
    search) echo 8083 ;;
    cart) echo 8084 ;;
    pricing) echo 8085 ;;
    inventory) echo 8086 ;;
    order) echo 8087 ;;
    payment) echo 8088 ;;
    notification) echo 8089 ;;
    analytics) echo 8090 ;;
    *) echo "" ;;
  esac
}

# --- Util functions ---

kill_port() {
  local port=$1
  if command -v lsof &>/dev/null; then
    local pids=$(lsof -ti :"$port" 2>/dev/null || true)
    if [ -n "$pids" ]; then
      echo -e "${GRAY}│  ├─${NC} ⨯ Clearing port $port..."
      echo "$pids" | xargs kill -9 2>/dev/null || true
    fi
  fi
}

check_port() {
  local port=$1
  local retries=15

  for i in $(seq 1 $retries); do
    if lsof -i :"$port" >/dev/null 2>&1; then
      echo -e "${GRAY}│  ├─${NC} ${GREEN}✓ Port $port active${NC}"
      return 0
    fi
    sleep 1
  done

  echo -e "${GRAY}│  ├─${NC} ${RED}⨯ Port $port failed to open${NC}"
  return 1
}

check_health() {
  local port=$1
  local retries=10

  for i in $(seq 1 $retries); do
    local status=$(curl -s "http://localhost:$port/actuator/health" | grep -o '"status":"UP"' || true)

    if [[ "$status" == *"UP"* ]]; then
      echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Healthcheck passed${NC}"
      return 0
    fi
    sleep 2
  done

  echo -e "${GRAY}│  └─${NC} ${RED}⨯ Healthcheck failed${NC}"
  return 1
}

start_service() {
  local name=$1
  local pom=$(service_pom "$name")

  if [ -z "$pom" ]; then
    echo -e "${RED}⨯ Unknown service: $name${NC}"
    exit 1
  fi

  local port=$(service_port "$name")

  echo -e "${GRAY}├─${NC} ${BLUE}●${NC} ${BOLD}Starting ${CYAN}$name${NC}"

  if [ -n "$port" ]; then
    kill_port "$port"
  fi

  local log_file="$LOG_DIR/${name}.log"
  echo -e "${GRAY}│  ├─${NC} ⚙ Booting Spring Boot process..."

  $MVN spring-boot:run -f "$pom" -q > "$log_file" 2>&1 &
  local pid=$!
  PIDS="$PIDS $pid"

  echo -e "${GRAY}│  ├─${NC} ${GRAY}PID: $pid → logs: $log_file${NC}"

  # Check port
  if [ -n "$port" ]; then
    check_port "$port"
    check_health "$port"
  fi
}

# -- MAIN --

MODE="${1:-all}"
ALL="gateway auth catalog search cart pricing inventory order payment notification analytics"

if [ "$MODE" = "all" ]; then
  SERVICES="$ALL"
else
  SERVICES="gateway $MODE"
fi

echo -e "\n${CYAN}${BOLD}▲ ShopFlux Backend Environment${NC}"
echo -e "${GRAY}│ Starting Services: ${WHITE}${SERVICES}${NC}"
echo -e "${GRAY}│${NC}"

# Start gateway first
if echo "$SERVICES" | grep -q "gateway"; then
  start_service gateway
  echo -e "${GRAY}│${NC}"
  sleep 2
fi

# Start remaining services
for svc in $SERVICES; do
  if [ "$svc" = "gateway" ]; then continue; fi
  start_service "$svc"
  echo -e "${GRAY}│${NC}"
  sleep 1
done

echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Backend is live${NC} ${WHITE}Gateway @ http://localhost:8080${NC}"
echo -e "\n${GRAY}Waiting for changes... Press Ctrl+C to exit.${NC}\n"

wait