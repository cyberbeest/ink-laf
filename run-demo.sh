#!/bin/bash
# Launches the SwingSet-style demo, rebuilding only if a source is newer than the build.
#   ./run-demo.sh              starts in Ink, with its own window frame
#   ./run-demo.sh metal 2      starts in Metal at UI scale 2 (HiDPI test); also: nimbus
cd "$(dirname "$0")" || exit 1
if [ ! -d out ] || [ -n "$(find src -name '*.java' -newer out -print -quit)" ]; then
    ./build.sh || exit 1
fi
laf=ink; scale=
for a in "$@"; do
    if [[ "$a" =~ ^[0-9.]+$ ]]; then scale=$a; else laf=$a; fi
done
[ -n "$scale" ] && export JAVA_TOOL_OPTIONS="-Dsun.java2d.uiScale=$scale"
exec java -cp out demo.Demo "$laf"
