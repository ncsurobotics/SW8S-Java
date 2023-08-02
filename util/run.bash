#!/bin/bash

tar --overwrite -xf "$(dirname $0)"/../app/build/distributions/app-unspecified.tar -C /tmp &&
	nohup /tmp/app-unspecified/bin/app "${@:1}"
