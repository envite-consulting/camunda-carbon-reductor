FROM maven:3.8.3-openjdk-17 AS BUILD
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim AS RUN

COPY --from=BUILD camunda-carbon-reductor-c8/target/camunda-carbon-reductor-c8-*.jar camunda-carbon-reductor-c8.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c8.jar"]