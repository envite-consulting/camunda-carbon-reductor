# 🌱Camunda Carbon Reductor - Camunda 7

Camunda 7 [External Task Worker](https://docs.camunda.org/manual/7.18/user-guide/process-engine/external-tasks/) adapter implementation of the Carbon Reductor.
Provides also a Service Task Template for configuration ([carbon-reductor.json](../exampleprocess/c7/.camunda/element-templates/carbon-reductor.json))

# Table of Contents

* 🚀 [Getting Started](#getting-started)
    * [Create a WattTime Account](#create-a-watttime-account)
    * [Run Connector locally](#run-the-external-task-worker-locally)
    * [Building the Docker containers](#building-the-docker-containers)

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

## Start Camunda Carbon Reductor
Add the WattTime credentials in the
[docker-compose File](./docker-compose.yaml) and start the
docker container by running `docker-compose up`.

The docker-compose contains an example Camunda Platform 7 Engine.
You can start the example process manually via the Camunda [Tasklist](http://localhost:7777/camunda/app/tasklist/), login as user with **username** `admin` and **password** `pw`.


## Run the External Task Worker locally

Configure the application using [application.yml](./src/main/resources/application.yml).

You can run the External Task Worker and connect it to a Camunda Platform 7.

```yml
camunda.bpm:
  client:
    base-url: <base_url>
    async-response-timeout: <async_response_timeout>
    worker-id: <worker_id>

```

## Building the Docker containers

To build the containers locally, you simply need to build them via the
`docker-compose` file to keep the parent module scope:

```bash
# Build all containers
docker-compose build

# Build specific container 
docker-compose build <service-name; e.g. camunda-7-carbon-reductor-connector>
```
