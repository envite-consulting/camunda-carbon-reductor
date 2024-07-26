FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim AS run

COPY --from=build camunda-carbon-reductor-c7/target/camunda-carbon-reductor-c7-*.jar camunda-carbon-reductor-c7.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c7.jar"]