# ---- Build stage ----
# Changed from jre to eclipse-temurin (JDK 21) on Alpine
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /sampleTrackingLimsApp
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run stage ----
# JRE is perfectly fine here for running the compiled JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /sampleTrackingLimsApp
# Using a explicit wildcard or specific name matches best practices
COPY --from=build /sampleTrackingLimsApp/target/*.jar sampleTrackingLimsApp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "sampleTrackingLimsApp.jar"]
