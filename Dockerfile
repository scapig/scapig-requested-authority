FROM openjdk:8

COPY target/universal/tapi-requested-authority-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf tapi-requested-authority-*.tgz

EXPOSE 7060