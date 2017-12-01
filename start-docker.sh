#!/bin/sh
SCRIPT=$(find . -type f -name tapi-requested-authority)
exec $SCRIPT -Dhttp.port=7060
