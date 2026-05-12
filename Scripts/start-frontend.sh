#!/usr/bin/env bash
set -e

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
FRONTEND_DIR="$ROOT_DIR/frontend"
PIDS=""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
NC='\033[0m'
BOLD='\033[1m'

cleanup() {
  echo -e "\n${CYAN}${BOLD}▲ ShopFlux Frontend Teardown${NC}"
  echo -e "${GRAY}│${NC}"
  echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Cleaning up processes${NC}"
  echo -e "${GRAY}│  ├─${NC} Stopping Vite instances..."
  for pid in $PIDS; do kill "$pid" 2>/dev/null || true; done
  wait 2>/dev/null || true
  echo -e "${GRAY}│  └─${NC} ${GREEN}✓ All processes terminated${NC}"
  echo -e "${GRAY}│${NC}"
  echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Frontend gracefully stopped${NC}\n"
}
trap cleanup EXIT INT TERM


mfe_dir() {
  case "$1" in
    product) echo "product-mfe" ;;
    search) echo "search-mfe" ;;
    cart) echo "cart-mfe" ;;
    order) echo "order-mfe" ;;
    admin) echo "admin-mfe" ;;
    *) echo "" ;;
  esac
}

mfe_port() {
  case "$1" in
    product) echo 3001 ;;
    search) echo 3002 ;;
    cart) echo 3003 ;;
    order) echo 3004 ;;
    admin) echo 3005 ;;
    *) echo "" ;;
  esac
}

ALL_MFES="product search cart order admin"


MODE="${1:-all}"

if [ "$MODE" = "all" ]; then
  MFES_TO_START="$ALL_MFES"
else
  MFES_TO_START=""
  OLD_IFS="$IFS"; IFS=','
  for t in $MODE; do
    IFS="$OLD_IFS"
    t=$(echo "$t" | tr -d ' ')
    dir=$(mfe_dir "$t")
    if [ -z "$dir" ]; then
      echo -e "${RED}⨯ Unknown MFE: $t${NC}"
      echo "Valid names: product search cart order admin"
      exit 1
    fi
    MFES_TO_START="$MFES_TO_START $t"
  done
  IFS="$OLD_IFS"
fi

echo -e "\n${CYAN}${BOLD}▲ ShopFlux Frontend Environment${NC}"
echo -e "${GRAY}│ Starting MFEs: ${WHITE}${MFES_TO_START}${NC}"
echo -e "${GRAY}│${NC}"


echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Dependencies${NC}"
for mfe in $MFES_TO_START; do
  dir=$(mfe_dir "$mfe")
  if [ ! -d "$FRONTEND_DIR/$dir/node_modules" ]; then
    echo -e "${GRAY}│  ├─${NC} ↓ Installing deps for ${CYAN}$dir${NC}..."
    (cd "$FRONTEND_DIR/$dir" && npm install --silent)
  fi
done

if [ ! -d "$FRONTEND_DIR/shell/node_modules" ]; then
  echo -e "${GRAY}│  ├─${NC} ↓ Installing deps for ${CYAN}shell${NC}..."
  (cd "$FRONTEND_DIR/shell" && npm install --silent)
fi
echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Dependencies ready${NC}"
echo -e "${GRAY}│${NC}"


echo -e "${GRAY}├─${NC} ${BLUE}●${NC} ${BOLD}Build Phase${NC}"
for mfe in $MFES_TO_START; do
  dir=$(mfe_dir "$mfe")
  echo -e "${GRAY}│  ├─${NC} ⚙ Building ${CYAN}$dir${NC}..."
  (cd "$FRONTEND_DIR/$dir" && npx vite build --logLevel error)
done

echo -e "${GRAY}│  ├─${NC} ⚙ Building ${CYAN}shell${NC}..."
(cd "$FRONTEND_DIR/shell" && npx vite build --logLevel error)
echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Build successful${NC}"
echo -e "${GRAY}│${NC}"


kill_port() {
  local port="$1"
  local pids
  pids=$(lsof -ti :"$port" 2>/dev/null || true)
  if [ -n "$pids" ]; then
    echo -e "${GRAY}│  ├─${NC} ⨯ Clearing port $port..."
    echo "$pids" | xargs kill -9 2>/dev/null || true
    sleep 1
  fi
}


echo -e "${GRAY}├─${NC} ${GREEN}●${NC} ${BOLD}Development Servers${NC}"
for mfe in $MFES_TO_START; do
  dir=$(mfe_dir "$mfe")
  port=$(mfe_port "$mfe")
  kill_port "$port"
  (cd "$FRONTEND_DIR/$dir" && npx vite preview --port "$port" --strictPort --host) > /dev/null 2>&1 &
  pid=$!
  PIDS="$PIDS $pid"
  echo -e "${GRAY}│  ├─${NC} ${GREEN}▶${NC} ${CYAN}$dir${NC} ${GRAY}(Port: $port) → PID: $pid${NC}"
done

sleep 2

kill_port 3000
(cd "$FRONTEND_DIR/shell" && npx vite preview --port 3000 --strictPort --host) > /dev/null 2>&1 &
shell_pid=$!
PIDS="$PIDS $shell_pid"
echo -e "${GRAY}│  ├─${NC} ${GREEN}▶${NC} ${CYAN}Shell (Gateway)${NC} ${GRAY}(Port: 3000) → PID: $shell_pid${NC}"

echo -e "${GRAY}│${NC}"
echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Frontend is live${NC} ${WHITE}http://localhost:3000${NC}"
echo -e "\n${GRAY}Waiting for changes... Press Ctrl+C to exit.${NC}\n"

wait