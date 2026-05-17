# Multi-stage build: compile + runtime                                                                                   
  FROM maven:3.9.6-eclipse-temurin-17 AS builder

  WORKDIR /build

  # Copier le pom parent et le pom du service
  COPY pom.xml ./
  COPY futurekawa-backend-core/pom.xml ./futurekawa-backend-core/
  COPY futurekawa-backend-core/src ./futurekawa-backend-core/src

  # Builder le JAR
  RUN mvn clean package -f futurekawa-backend-core/pom.xml -DskipTests

  # Runtime image (sans alpine pour compatibilité Mac/ARM)                                                                 
  FROM eclipse-temurin:17-jre
                                                                                                                           
  WORKDIR /app    
                                                                                                                           
  # Copier le JAR depuis l'étape de build
  COPY --from=builder /build/futurekawa-backend-core/target/*.jar app.jar
                                                                                                                           
  # Expose le port
  EXPOSE 8080                                                                                                              
                  
  # Health check
  HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1                                 
   
  # Lancer l'application                                                                                                   
  ENTRYPOINT ["java", "-jar", "app.jar"]