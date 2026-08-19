# Stage 1: Build
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . ./discoveryserver
WORKDIR /app/discoveryserver
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/discoveryserver/target/*.jar discoveryserver.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "discoveryserver.jar"]