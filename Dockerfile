# ===== BUILDER =====
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Configurar UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Forzar UTF-8 en Maven
RUN mvn clean package -DskipTests -B -Dfile.encoding=UTF-8

# ===== RUNTIME =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "-jar", "app.jar"]