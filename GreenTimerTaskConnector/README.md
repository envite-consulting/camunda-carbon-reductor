## Green Timer Task Connector

# Run Carbon Aware SDK Web API locally

You must start the carbon aware SDK locally which is a proxy to the WattTime-API:

Create Docker image:

```bash
git clone https://github.com/Green-Software-Foundation/carbon-aware-sdk.git
cd src/CarbonAware.WebApi/src
docker build -t carbon-aware-sdk-webapi -f CarbonAware.WebApi/src/Dockerfile .
```

Run Docker image:

Note: Replace `wattTimeUsername` and `wattTimePassword` with your individual credentials in the following command.

```bash
docker run -it --rm -p 8090:80 \
    -e CarbonAwareVars__CarbonIntensityDataSource="WattTime" \
    -e WattTimeClient__Username="wattTimeUsername" \
    -e WattTimeClient__Password="wattTimePassword" \
  carbon-aware-sdk-webapi
```

Test the API with `curl -s "http://localhost:8090/emissions/forecasts/current?location=westus2"`. This should return a lengthy JSON response.

Congratulations - the API is now running locally.

# Run Connector
```bash
mvn clean package
java -cp 'target/deployment/DelayForGreenEnergyConnector-1.0.0-SNAPSHOT.jar' io.camunda.connector.runtime.jobworker.Main
```

## Environment Variables
* ZEEBE_ADDRESS (e.g. c8-carbon-hack-zeebe-gateway:26500)
* ZEEBE_INSECURE (e.g. true)
* CARBON_AWARE_SDK_BASEPATH (e.g. http://localhost:8090)
