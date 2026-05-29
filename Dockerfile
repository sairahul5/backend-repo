# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download its dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the application source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime environment
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create directory for file uploads
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# Copy the built JAR from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
