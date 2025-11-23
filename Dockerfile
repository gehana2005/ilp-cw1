# 1. Build stage: uses Maven to compile your Spring Boot app
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (produces a fat JAR)
RUN mvn -q clean package -DskipTests

# 2. Runtime stage: use a lightweight JDK image
FROM eclipse-temurin:21-jre-alpine

# Create directory inside container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (change if your server.port is different)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
