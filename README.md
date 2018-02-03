## scapig-requested-authority

This is the microservice which stores and retrieve the transient requested authority which is created during the Oauth 2.0 user authorization journey.
It is part of the Scapig API Manager (http://www.scapig.com)

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t scapig-requested-authority .
``

## Publishing
``
docker tag scapig-requested-authority scapig/scapig-requested-authority
docker login
docker push scapig/scapig-requested-authority
``

## Running
``
docker run -p9014:9014 -d scapig/scapig-requested-authority
``
