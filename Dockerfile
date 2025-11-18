# Use the official Eclipse Temurin JDK 21 (LTS) on Alpine Linux
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot JAR from the target folder
COPY target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
