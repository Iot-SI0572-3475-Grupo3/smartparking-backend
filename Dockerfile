# ===== ETAPA 1: CONSTRUCCIÓN =====
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar solo pom.xml primero (mejor cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir JAR (sin tests)
RUN mvn clean package -DskipTests -B

# ===== ETAPA 2: IMAGEN FINAL (LIGERA) =====
FROM eclipse-temurin:21-jre-alpine

# Metadatos
LABEL maintainer="tu-email@upc.edu.pe" \
      version="1.0" \
      description="SmartParking Backend - Render Deployment"

WORKDIR /app

# Copiar JAR
COPY --from=builder /app/target/*.jar app.jar

# Puerto (Render lo asigna, pero exponemos 8080)
EXPOSE 8080

# Healthcheck para Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]