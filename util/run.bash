#!/bin/bash

tar -xf "$(dirname $0)"/../app/build/distributions/app-unspecified.tar -C /tmp &&
	/tmp/app-unspecified/bin/app "${@:1}"
