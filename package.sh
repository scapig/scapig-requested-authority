#!/bin/sh
sbt universal:package-zip-tarball
docker build -t scapig-requested-authority .
docker tag scapig-requested-authority scapig/scapig-requested-authority:0.1
docker push scapig/scapig-requested-authority:0.1
