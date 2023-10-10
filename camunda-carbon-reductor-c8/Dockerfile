FROM eclipse-temurin:17-jdk AS BUILD
COPY . .
RUN ./mvnw clean package

FROM eclipse-temurin:17-jdk AS RUN

COPY --from=BUILD camunda-carbon-reductor-c8/target/camunda-carbon-reductor-c8-*.jar camunda-carbon-reductor-c8.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c8.jar"]