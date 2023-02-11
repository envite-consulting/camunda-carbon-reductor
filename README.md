# 🌱Camunda Carbon Reductor

The Camunda Carbon Reductor allows you to time shift your processes' carbon emissions when energy is clean while still fulfilling the requested SLAs.

Technically, it's implemented as a Camunda [Connector](https://docs.camunda.io/docs/components/connectors/introduction-to-connectors/) for Camunda Platform 8 (and can easily be ported to Camunda Platform 7).

# Table of Contents

* ✨ [Features](#features)
* 🚀 [Getting Started](#getting-started)
  * [Camunda 8](#camunda-8)
  * [Camunda 7](#camunda-7)
* 📈 [Reporting Carbon Reduction via Camunda Optimize](#reporting-carbon-reduction-via-camunda-optimize)
* 📚 [Releases](#releases)
* 📆 [Publications](#publications)
* 📨 [Contact](#contact)

# ✨Features

The Carbon Reductor Connector consists of an element-template that can be used in the process 
model that connects to the connector function. The Carbon Reductor Connector allows you to
optimize for lower carbon emissions by moving your process execution into a timeframe with the least amount 
of carbon possible while still fulfilling SLAs.

The Carbon Reductor Connector defines the following inputs:

- the location where the worker are running (e.g. Germany, UK, France, USA, etc.)
- a milestone (a timestamp used to calculate the duration the process instance has been running)
- a duration for the remainder of the process (the duration the remainder needs at most)
- the maximum duration a process instance can take

# 🚀Getting Started

We provide the Carbon Reductor for Camunda Platform 7 and 8:

## Camunda 8

To start the Camunda 8 Connector have a look at the specific [README](./camunda-carbon-reductor-c8/README.md).

## Camunda 7

To start the Camunda 7 External Task Worker have a look at the specific [README](./camunda-carbon-reductor-c7/README.md).

# 📈Reporting Carbon Reduction via Camunda Optimize

Since Camunda Carbon Reductor stores the carbon savings as process variables, 
we have the ability to visualize our successes in [Camunda Optimize](https://camunda.com/de/platform/optimize/).

For the [CarbonHack22](https://taikai.network/gsf/hackathons/carbonhack22/projects/cl9czuvwy65500401uzm9hfwbs9/idea) 
we visualized the results for out example process and it looks like the following: 

![CarbonHack22 Dashboard](assets/CarbonHack22-Camunda-Optimize-Dashboard.png)

The exported Dashboard Definition could be found [here](assets/optimize-dashboard-definition.json).

# 📚Releases

Coming soon 🚀

# 📆Publications

* 2022-11: [Project Pitch](https://taikai.network/gsf/hackathons/carbonhack22/projects/cl9czuvwy65500401uzm9hfwbs9/idea)  
  Project pitch on Taikai as part of the [#CarbonHack22](https://greensoftware.foundation/articles/carbonhack22) hackathon
* 2022-11: [Carbon Reduced Business Process Execution](https://youtu.be/sGW5MJoOxPk)  
  2 Minute pitch on YouTube as part of the [#CarbonHack22](https://greensoftware.foundation/articles/carbonhack22) hackathon

# 📨Contact

If you have any questions or ideas feel free to create an [issue](https://github.com/envite-consulting/carbonaware-process-automation/discussions/issues) or contact us via GitHub Discussions or mail.

This open source project is being developed by [envite consulting GmbH](https://envite.de).

![envite consulting GmbH](assets/envite-black.png#gh-light-mode-only)
![envite consulting GmbH](assets/envite-white.png#gh-dark-mode-only)
