#!/bin/sh
SCRIPT=$(find . -type f -name scapig-requested-authority)
rm -f scapig-requested-authority*/RUNNING_PID
exec $SCRIPT -Dhttp.port=9014 $JAVA_OPTS -J-Xms16M -J-Xmx64m
