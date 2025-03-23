# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user
RUN addgroup -S datascope && adduser -S datascope -G datascope

# Create necessary directories
RUN mkdir -p /app/logs /app/config \
    && chown -R datascope:datascope /app

# Copy built artifact and configuration
COPY --from=build /app/data-scope-main/target/data-scope.jar /app/
COPY --from=build /app/data-scope-main/src/main/resources/application.yml /app/config/

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs"
ENV SPRING_CONFIG_LOCATION="file:/app/config/application.yml"
ENV SPRING_PROFILES_ACTIVE="prod"

# Switch to non-root user
USER datascope

# Expose ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/data-scope.jar"]