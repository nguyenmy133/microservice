# Stage 1: Use Maven for building
FROM maven:3.8.5-openjdk-17-slim as build
WORKDIR /app

COPY ./notificationservice ./notificationservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/notificationservice
RUN mvn clean package -DskipTests

# Stage 2: use OpenJDK for running
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/notificationservice/target/*.jar notificationservice.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","notificationservice.jar"]