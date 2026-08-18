FROM eclipse-temurin:21-jre
WORKDIR /app
COPY Intelli-Edu-resource/target/Intelli-Edu-resource-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8892
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
