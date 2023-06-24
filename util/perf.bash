#!/usr/bin/env bash

# Builds and records perf data for path processing
# You need perf, JDK >= 16, and inferno (https://github.com/jonhoo/inferno)
# https://bell-sw.com/announcements/2022/04/07/how-to-use-perf-to-monitor-java-performance/

./gradlew testPath &&
    perf record -g -- java -XX:+UnlockDiagnosticVMOptions -XX:+DumpPerfMapAtExit -jar app/build/libs/test-jar-path.jar &&
    perf script | inferno-collapse-perf | inferno-flamegraph > flamegraph.svg
