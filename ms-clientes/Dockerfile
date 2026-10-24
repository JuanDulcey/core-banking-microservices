# =============================================================================
# Stage 1: Build con Eclipse Temurin JDK 21 Alpine (Multi-Módulo)
# =============================================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

COPY pom.xml .
COPY ms-clientes/pom.xml ms-clientes/pom.xml
COPY ms-cuentas/pom.xml ms-cuentas/pom.xml
RUN mvn -B -pl ms-clientes -am dependency:go-offline

COPY ms-clientes/src ms-clientes/src
RUN mvn -B -pl ms-clientes -am clean package -DskipTests

# =============================================================================
# Stage 2: Runtime ligero y seguro (JRE 21 Alpine + Usuario no-root)
# =============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S banking && adduser -S spring -G banking
USER spring:banking

COPY --from=builder /workspace/ms-clientes/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
