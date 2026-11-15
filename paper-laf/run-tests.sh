#!/bin/bash
# Builds the library and runs the headless repaint checks.
cd "$(dirname "$0")" || exit 1
./build.sh || exit 1
rm -rf test-out && mkdir test-out
javac -cp out -d test-out test/*.java || exit 1
exec java -Djava.awt.headless=true -cp out:test-out RepaintTrailCheck
