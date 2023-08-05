#!/usr/bin/env bash

trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT
mpv --title=FRONT --no-cache --untimed --profile=low-latency --no-correct-pts --fps=60 --osc=no rtsp://192.168.2.5:8554/camFRONT
