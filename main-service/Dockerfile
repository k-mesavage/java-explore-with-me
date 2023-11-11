FROM amazoncorretto:17-alpine
COPY target/*.jar server.jar
ENTRYPOINT ["java", "-jar", "/server.jar"]