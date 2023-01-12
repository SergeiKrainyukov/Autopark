FROM openjdk:11-jre-slim-buster
COPY "/target/app-0.0.5.jar" .
ENTRYPOINT ["java", "-jar", "app-0.0.5.jar"]
