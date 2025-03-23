# Build stage
FROM maven:3.9.5-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Add Tini for proper signal handling
ENV TINI_VERSION v0.19.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]

# Create non-root user
RUN groupadd -r datascope && useradd -r -g datascope datascope

# Copy application files
COPY --from=build /app/data-scope-app/target/data-scope-app-*.jar app.jar

# Set proper permissions
RUN chown -R datascope:datascope /app

# Switch to non-root user
USER datascope

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Expose ports
EXPOSE 8080

# Start application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]