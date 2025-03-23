# Build stage
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Add maintainer info
LABEL maintainer="dreambt <your.email@example.com>"

# Set timezone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create app directory
RUN mkdir -p /app/logs /app/config

# Copy jar from build stage
COPY --from=build /app/data-scope-main/target/data-scope-main.jar /app/app.jar

# Copy config files
COPY --from=build /app/data-scope-main/src/main/resources/application.yml /app/config/
COPY --from=build /app/data-scope-main/src/main/resources/logback-spring.xml /app/config/

# Environment variables
ENV JAVA_OPTS="-Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE="prod"
ENV SPRING_CONFIG_LOCATION="file:/app/config/"

# Expose ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]