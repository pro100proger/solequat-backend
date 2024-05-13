FROM openjdk:17
EXPOSE 8765
COPY target/gateway.war /gateway.war
ENTRYPOINT ["java", "-jar", "/gateway.war"]