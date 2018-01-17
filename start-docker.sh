#!/bin/sh
SCRIPT=$(find . -type f -name scapig-requested-authority)
rm -f scapig-requested-authority*/RUNNING_PID
exec $SCRIPT -Dhttp.port=7060 -J-Xms128M -J-Xmx512m
