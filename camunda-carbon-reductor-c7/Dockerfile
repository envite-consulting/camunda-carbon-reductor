FROM maven:3.8.3-openjdk-17 AS BUILD
COPY . .
RUN mvn clean package

FROM openjdk:17.0.1-jdk-slim as RUN

COPY --from=BUILD camunda-carbon-reductor-c7/target/camunda-carbon-reductor-c7-*.jar camunda-carbon-reductor-c7.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c7.jar"]