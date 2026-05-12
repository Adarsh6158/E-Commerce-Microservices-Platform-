#!/usr/bin/env bash

GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
NC='\033[0m'
BOLD='\033[1m'

echo -e "\n${CYAN}${BOLD}▲ ShopFlux Frontend Teardown${NC}"
echo -e "${GRAY}│${NC}"

echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Cleaning up processes${NC}"

VITE_PIDS=$(pgrep -f "vite" 2>/dev/null | grep -v "$$" || true)
if [ -n "$VITE_PIDS" ]; then
  echo -e "${GRAY}│  ├─${NC} Stopping Vite instances..."
  echo "$VITE_PIDS" | xargs kill 2>/dev/null || true
  sleep 1
  echo -e "${GRAY}│  └─${NC} ${GREEN}✓ All processes terminated${NC}"
else
  echo -e "${GRAY}│  └─${NC} ${GRAY}No running frontend processes found${NC}"
fi

echo -e "${GRAY}│${NC}"
echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Frontend gracefully stopped${NC}\n"