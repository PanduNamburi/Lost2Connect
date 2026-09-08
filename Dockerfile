# Build stage: Compile Maven project with Java 21
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: Lightweight Alpine container with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/lost2found-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
