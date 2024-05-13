FROM openjdk:17
EXPOSE 8085
COPY target/business-logic.war /business-logic.war
ENTRYPOINT ["java", "-jar", "/business-logic.war"]