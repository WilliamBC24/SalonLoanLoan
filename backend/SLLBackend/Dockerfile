FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Download dependencies (this layer will be cached)
RUN ./gradlew dependencies --no-daemon

# Expose Spring Boot default port
EXPOSE 8080

# Run the application with continuous build for hot reload
CMD ["./gradlew", "bootRun", "--continuous"]
