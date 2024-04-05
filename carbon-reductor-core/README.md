# 🌱Camunda Carbon Reductor - Core

Core of the Carbon Reductor, providing a single input use case which calculates delay. 

Uses the [Carbon Aware API](../api/api-carbon-aware/README.md).

# Table of Contents

* 🔧[️Configuration](#configuration)
* 🏗[️Building Blocks](#building-blocks)

# 🔧️Configuration

The environmental variable `CARBON-REDUCTOR_CARBON-AWARE-API_BASE-PATH` allows you to set the URL of the Carbon Aware API.  
Default is `http://localhost:8090`.

Or you can override all properties in the [application.yaml](./src/main/resources/application.yaml) by applying e.g. a custom one. 


# 🏗Building Blocks

![Building Block View Level 1 Carbon Reductor Core](../assets/diagram/generated/building-block-view-level-1-carbon-reductor-core.png)

| Element                       | Description                                                                                                           |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| DelayCalculator               | Input port to calculate a time shift.                                                                                 |
| DelayCalculatorService        | Implementation of the input port calculating the time shift.                                                          |
| CarbonReductorConfiguration   | Aggregate representing the input configuration.                                                                       |
| CarbonReduction               | Aggregate representing the result of the time shift.                                                                  |
| CarbonEmissionQuery           | Output port allowing to query for carbon emission forecast data.                                                      |
| CarbonAwareSdkClient          | Implementation of the output port using the [Carbon Aware SDK](../api/api-carbon-aware/README.md)                     |
| CarbonAwareComputingApiClient | Implementation of the output port using the [Carbon Aware Computing API](../api/api-carbon-aware-computing/README.md) |
