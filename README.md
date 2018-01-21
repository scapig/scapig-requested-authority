## scapig-requested-authority

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
docker tag scapig-requested-authority scapig/scapig-requested-authority:VERSION
docker login
docker push scapig/scapig-requested-authority:VERSION
``

## Running
``
docker run -p9014:9014 -d scapig/scapig-requested-authority:VERSION
``
