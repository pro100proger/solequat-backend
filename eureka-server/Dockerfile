FROM openjdk:17
EXPOSE 8761
COPY target/eureka-server.war /eureka-server.war
ENTRYPOINT ["java", "-jar", "/eureka-server.war"]