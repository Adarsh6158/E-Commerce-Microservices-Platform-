#!/usr/bin/env bash

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
NC='\033[0m'
BOLD='\033[1m'

if command -v docker &>/dev/null && docker compose version &>/dev/null; then
  COMPOSE="docker compose"
elif command -v podman-compose &>/dev/null; then
  COMPOSE="podman-compose"
else
  COMPOSE=""
fi

echo -e "\n${CYAN}${BOLD}▲ ShopFlux Backend Teardown${NC}"
echo -e "${GRAY}│${NC}"

echo -e "${GRAY}├─${NC} ${YELLOW}●${NC} ${BOLD}Stopping Microservices${NC}"

MAVEN_PIDS=$(pgrep -f "spring-boot:run" 2>/dev/null || true)
if [ -n "$MAVEN_PIDS" ]; then
  echo -e "${GRAY}│  ├─${NC} Terminating Spring Boot processes..."
  echo "$MAVEN_PIDS" | xargs kill 2>/dev/null || true
  sleep 2
  REMAINING=$(pgrep -f "spring-boot:run" 2>/dev/null || true)
  if [ -n "$REMAINING" ]; then
    echo -e "${GRAY}│  ├─${NC} Force-killing survivors..."
    echo "$REMAINING" | xargs kill -9 2>/dev/null || true
  fi
  echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Services terminated${NC}"
else
  echo -e "${GRAY}│  └─${NC} ${GRAY}No running services found${NC}"
fi

if [ "$1" = "--infra" ]; then
  echo -e "${GRAY}│${NC}"
  echo -e "${GRAY}├─${NC} ${BLUE}●${NC} ${BOLD}Stopping Infrastructure${NC}"
  if [ -n "$COMPOSE" ]; then
    echo -e "${GRAY}│  ├─${NC} Executing teardown via $COMPOSE..."
    $COMPOSE -f Infra/docker-compose.yml down > /dev/null 2>&1
    echo -e "${GRAY}│  └─${NC} ${GREEN}✓ Infrastructure offline${NC}"
  else
    echo -e "${GRAY}│  └─${NC} ${RED}⨯ No compose tool found${NC}"
  fi
fi

echo -e "${GRAY}│${NC}"
echo -e "${GRAY}└─${NC} ${GREEN}${BOLD}✓ Backend gracefully stopped${NC}\n"