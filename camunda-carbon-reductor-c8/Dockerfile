FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim AS run

COPY --from=build camunda-carbon-reductor-c8/target/camunda-carbon-reductor-c8-*.jar camunda-carbon-reductor-c8.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c8.jar"]