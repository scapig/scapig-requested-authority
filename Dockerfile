FROM openjdk:8

COPY target/universal/scapig-requested-authority-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf scapig-requested-authority-*.tgz

EXPOSE 7060

CMD ["sh", "start-docker.sh"]