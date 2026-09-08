# Build stage: Compile Maven project with Java 21 on glibc (Debian Jammy)
FROM maven:3.9.8-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: Temurin 21 JRE Jammy (Ubuntu/Debian glibc) required for gRPC / Firebase Netty native SSL JNI
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/lost2found-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
