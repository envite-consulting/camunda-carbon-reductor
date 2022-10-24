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

# Run Connector locally

Configure the application using [application.properties](/src/main/resources/application.properties) for:

1. Camunda Cloud

```properties
zeebe.client.cloud.cluster-id=xxx
zeebe.client.cloud.client-id=xxx
zeebe.client.cloud.client-secret=xxx
zeebe.client.cloud.region=dsm-1
```
2. local Camunda Instance

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```