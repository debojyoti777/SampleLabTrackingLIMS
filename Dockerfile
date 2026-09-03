# Docker file , do not change unless absolutely necessary


# ---- Build stage ----
# Changed from jre to eclipse-temurin (JDK 21) on Alpine
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /sampleLabTrackingLimsApp
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package

# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /sampleTrackingLimsApp

COPY --from=build /sampleLabTrackingLimsApp/target/*.jar sampleLabTrackingLimsApp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "sampleLabTrackingLimsApp.jar"]
