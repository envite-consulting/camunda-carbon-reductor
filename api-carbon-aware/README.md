# 🌱Camunda Carbon Reductor - Carbon Aware API

Module to wrap the [Carbon Aware API](https://github.com/Green-Software-Foundation/carbon-aware-sdk) which is used 
to collect the information used to calculate the delay. 

# Table of Contents

* 🗒 [Notes](#notes)
* 🚀 [Getting Started](#getting-started)
    * [Run Carbon Aware SDK Web API locally](#run-carbon-aware-sdk-web-api-locally)

# 🗒️Notes

We are planning to support multiple APIs to get the information needed to
calculate the delay.

# 🚀Getting Started

## Run Carbon Aware SDK Web API locally

Start the carbon aware SDK locally which acts as a proxy to the WattTime-API:

Run Docker image:

Note: Replace `<myWattTimeUsername>` and `<myWattTimePassword>` with your individual credentials in the following command.

```bash
docker run -it --rm -p 8090:80 \
    -e DataSources__EmissionsDataSource="WattTime" \
    -e DataSources__ForecastDataSource="WattTime" \
    -e DataSources__Configurations__WattTime__Type="WattTime" \
    -e DataSources__Configurations__WattTime__Username="<myWattTimeUsername>" \
    -e DataSources__Configurations__WattTime__Password="<myWattTimePassword>" \
  enviteconsulting/carbon-aware-sdk-webapi
```

Test the API with `curl -s "http://localhost:8090/emissions/forecasts/current?location=westus"`.
This should return a lengthy JSON response.

Congratulations 🎉 - the API is now running locally.