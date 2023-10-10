FROM eclipse-temurin:17-jdk AS BUILD
COPY . .
RUN ./mvnw clean package

FROM eclipse-temurin:17-jdk AS RUN

COPY --from=BUILD camunda-carbon-reductor-c7/target/camunda-carbon-reductor-c7-*.jar camunda-carbon-reductor-c7.jar

ENTRYPOINT ["java", "-jar", "camunda-carbon-reductor-c7.jar"]