#!/bin/sh
sbt universal:package-zip-tarball
docker build -t scapig-requested-authority .
docker tag scapig-requested-authority scapig/scapig-requested-authority
docker push scapig/scapig-requested-authority
