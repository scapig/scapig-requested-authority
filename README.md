## tapi-requested-authority

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t tapi-requested-authority .
``

## Running
``
docker run -p7060:7060 -i -a stdin -a stdout -a stderr tapi-requested-authority sh start-docker.sh
``