FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/weather-bot.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]