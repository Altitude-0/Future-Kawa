# Multi-stage build: compile + runtime
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml ./
COPY futurekawa-alerting-service/pom.xml ./futurekawa-alerting-service/
COPY futurekawa-mqtt-service/pom.xml ./futurekawa-mqtt-service/
COPY futurekawa-backend-core/pom.xml ./futurekawa-backend-core/

# Copier le code source
COPY futurekawa-alerting-service/src ./futurekawa-alerting-service/src

# Builder le JAR
RUN mvn clean package -f futurekawa-alerting-service/pom.xml -DskipTests

# Runtime image (sans alpine pour compatibilité Mac/ARM)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /build/futurekawa-alerting-service/target/*.jar app.jar

# Expose le port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
