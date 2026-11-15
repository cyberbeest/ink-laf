#!/bin/bash
# Compiles the Scroll & Ink L&F and the demo into ./out
cd "$(dirname "$0")" && rm -rf out && mkdir out && javac -Xlint:all,-serial,-this-escape -d out $(find src -name '*.java')
