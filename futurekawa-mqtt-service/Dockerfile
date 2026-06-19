# Multi-stage build: compile + runtime
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml ./
COPY futurekawa-mqtt-service/pom.xml ./futurekawa-mqtt-service/
COPY futurekawa-backend-core/pom.xml ./futurekawa-backend-core/

# Copier le code source
COPY futurekawa-mqtt-service/src ./futurekawa-mqtt-service/src

# Builder le JAR
RUN mvn clean package -f futurekawa-mqtt-service/pom.xml -DskipTests

# Runtime image (sans alpine pour compatibilité Mac/ARM)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /build/futurekawa-mqtt-service/target/*-exec.jar app.jar

# Expose le port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
