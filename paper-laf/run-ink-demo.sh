#!/bin/bash
# Launches the demo directly in the Ink look and feel (optional UI scale as argument).
exec "$(dirname "$0")/run-demo.sh" ink "$@"
