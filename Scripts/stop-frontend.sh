#!/usr/bin/env bash

echo "======================================"
echo " Stopping Frontend Servers"
echo "======================================"

# Kill vite preview and vite dev processes
VITE_PIDS=$(pgrep -f "vite" 2>/dev/null | grep -v "$$" || true)
if [ -n "$VITE_PIDS" ]; then
  echo "Stopping Vite processes..."
  echo "$VITE_PIDS" | xargs kill 2>/dev/null || true
  echo "Frontend processes stopped."
else
  echo "No running frontend processes found."
fi

echo "Done."