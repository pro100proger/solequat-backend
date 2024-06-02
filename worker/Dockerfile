FROM openjdk:17
EXPOSE 8084
COPY target/worker.war /worker.war
ENTRYPOINT ["java", "-jar", "/worker.war"]