# 🌱Camunda Carbon Reductor - Carbon Aware API

Module to wrap the [Carbon Aware API](https://github.com/Green-Software-Foundation/carbon-aware-sdk) which could be used 
to collect the information to calculate the delay. 

# Table of Contents

* 🗒 [Notes](#notes)
* 🚀 [Getting Started](#getting-started)
    * [Create a WattTime Account](#create-a-watttime-account)
    * [Run Carbon Aware SDK Web API locally](#run-carbon-aware-sdk-web-api-locally)

# 🗒️Notes

We are planning to support multiple APIs to get the information needed to
calculate the delay.

# 🚀Getting Started

## Create a WattTime Account
The Account can be created with a POST request ([watttime.org/api-documentation](https://www.watttime.org/api-documentation/#best-practices-for-api-usage)). With the visitor plan you are allowed to query information for the
CAISO_NORTH (California) region.

```bash
curl -X POST --location "https://api2.watttime.org/v2/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"<myWattTimeUsername>\",
          \"password\": \"<myWattTimePassword>\",
          \"email\": \"<myEmailAddress>\"}"
```

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
  ghcr.io/green-software-foundation/carbon-aware-sdk
```

Test the API with `curl -s "http://localhost:8090/emissions/forecasts/current?location=westus"`.
This should return a lengthy JSON response.

Congratulations 🎉 - the API is now running locally.