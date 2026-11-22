
# Stage 1: Build application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src ./src

# Build executable JAR
RUN mvn clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/employee-microservice-1.0.0.jar app.jar

# Create database directory
RUN mkdir -p /app/data

# Application port
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
