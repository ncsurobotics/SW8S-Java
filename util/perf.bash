#!/usr/bin/env bash

# Builds and records perf data for path processing
# You need perf, JDK >= 16, and inferno (https://github.com/jonhoo/inferno)
# https://bell-sw.com/announcements/2022/04/07/how-to-use-perf-to-monitor-java-performance/

./gradlew --offline testPath &&
    perf record -qg -- java -XX:+UnlockDiagnosticVMOptions -XX:+DumpPerfMapAtExit -XX:+PrintCompilation -jar app/build/libs/test-jar-path.jar &&
    if [[ -n $(command -v inferno-flamgraph) ]]; then
        perf script | inferno-collapse-perf | inferno-flamegraph > flamegraph.svg
    else
        perf script > out.script
    fi
