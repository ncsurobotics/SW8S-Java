#!/usr/bin/env bash

ssh sw8@192.168.2.5 "sudo date --set @$(date -u +%s)"
