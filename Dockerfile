FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/trancs-0.0.1-SNAPSHOT.jar /app/trancs.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/trancs.jar"]
