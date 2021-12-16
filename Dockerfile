FROM openjdk:17-alpine
COPY docker/app/erapulus-server.jar /opt/app/erapulus-server.jar
ENTRYPOINT exec java -jar /opt/app/erapulus-server.jar