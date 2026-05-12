#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
RED='\033[0;31m'
NC='\033[0m'
BOLD='\033[1m'

if command -v docker &>/dev/null && docker compose version &>/dev/null; then
  COMPOSE="docker compose"
elif command -v podman-compose &>/dev/null; then
  COMPOSE="podman-compose"
else
  COMPOSE=""
fi

echo -e "\n${CYAN}${BOLD}▲ ShopFlux Infrastructure Teardown${NC}"
echo -e "${GRAY}│${NC}"

echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Stopping Containers${NC}"

if [ -n "$COMPOSE" ]; then
  echo -e "${GRAY}│  ├─${NC} Executing teardown via $COMPOSE...\n"
  if $COMPOSE -f Infra/docker-compose.yml down; then
    echo -e "\n${GRAY}│  └─${NC} ${GREEN}✓ Infrastructure offline${NC}"
  else
    echo -e "\n${GRAY}│  └─${NC} ${RED}⨯ Infrastructure teardown failed${NC}"
    exit 1
  fi
else
  echo -e "${GRAY}│  └─${NC} ${RED}⨯ No compose tool found${NC}"
  exit 1
fi

echo -e "${GRAY}│${NC}"
echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Infrastructure gracefully stopped${NC}\n"
