FROM openjdk:17-jdk-slim
LABEL authors="Ayoub"
WORKDIR /app
COPY target/eventsProject-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]