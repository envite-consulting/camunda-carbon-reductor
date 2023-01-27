# 🌱Camunda Carbon Reductor - Camunda 8

Camunda 8 [Connector](https://docs.camunda.io/docs/components/connectors/introduction-to-connectors/) adapter 
implementation of the Carbon Redcutor.

# Table of Contents

* 🗒 [Notes](#notes)
* 🚀 [Getting Started](#getting-started)
  * [Create a cluster and client](#create-a-cluster-and-client)
  * [Import Process Model to Camunda Platform 8](#import-process-model-to-camunda-platform-8)
  * [Create a WattTime Account](#create-a-watttime-account)
  * [Run Connector locally](#run-connector-locally)

# 🗒️Notes

Right now it's kind of a job-worker-connector, due to the fact we need
to delay it with the `retryBackoff`.

`TODO`: Migrate to the "real" Camunda 8 [Connector](https://docs.camunda.io/docs/components/connectors/custom-built-connectors/connector-sdk/) if 
it provides the functionality to allow delaying the execution.

# 🚀Getting Started

Add the WattTime credentials as well as the Camunda SaaS one in the 
[docker-compose File](./docker-compose.yaml) and start the 
docker container by running `docker-compose up`.

## Create a Cluster and Client

* Login to Camunda Platform 8: https://camunda.io/
* Create a cluster
* Register a new client in the section "API". Select all scopes if unsure.

## Import Process Model to Camunda Platform 8

* Open the Camunda Web Modeler
* Import the process model `exampleprocess/NasaImageProcessing.bpmn`
* Start a new instance
* Switch to Camunda Operate to see the token waiting at the connector

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

## Run Connector locally

Configure the application using [application.yml](/application.yml).

You can run the Connector and connect it to a Camunda Platform 8 SaaS cluster.

```yml
zeebe:
  client:
    cloud:
      clientId: xxx
      clusterId: xxx
      clientSecret: xxx
      region: dsm-1
```

If you're running Camunda Platform 8 Self-Managed then use the following configuration:

```yml
zeebe:
  client:
    broker.gateway-address: 127.0.0.1:26500
    security.plaintext: true
```

Once the Connector is running you will see log entries like the following.

In case of a time window with dirty energy:
```
Time shifting job 4503599628706752 by PT1M30S
...
Completing previously time shifted job 4503599628706752
```

In case of a time window with clean energy:
```
Executing job 4503599628706759 immediately
```
