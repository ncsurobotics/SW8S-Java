#!/usr/bin/env sh

paste -d '\n' '/tmp/Comms_Out.log' '/tmp/Comms_In.log' |
    column -L -s '|' -t
