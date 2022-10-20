## Green Timer Task Connector
# Run Connector
```bash
mvn clean package
java -cp 'target/deployment/DelayForGreenEnergyConnector-1.0.0-SNAPSHOT.jar' io.camunda.connector.runtime.jobworker.Main
```

## Environment Variables
* ZEEBE_ADDRESS (e.g. c8-carbon-hack-zeebe-gateway:26500)
* ZEEBE_INSECURE (e.g. true)
* CARBON_AWARE_SDK_BASEPATH (e.g. http://localhost:8090)

## Run Carbon Aware SDK Container
```bash
docker run -it --rm -p 8090:80 \
    -e CarbonAwareVars__CarbonIntensityDataSource="WattTime" \
    -e WattTimeClient__Username="wattTimeUsername" \
    -e WattTimeClient__Password="wattTimePassword" \
    carbon-aware-sdk-webapi
```
