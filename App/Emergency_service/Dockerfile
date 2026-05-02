# ---- Stage 1: Build ----
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests


# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]