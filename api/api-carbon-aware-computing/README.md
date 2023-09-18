# 🌱Camunda Carbon Reductor - Carbon Aware Computing

Module to wrap the [Carbon Aware Computing](https://www.carbon-aware-computing.com/) API which could be used 
to collect the information to calculate the delay. 

# Table of Contents

* 🗒 [Notes](#notes)

# 🗒️Notes

The Carbon Aware Computing API yet not supports the exact same locations as the 
[Carbon Aware SDK](../api-carbon-aware/README.md). To have similar user experience setting the locations
in the element template we map the locations of the Carbon Aware SDK, to the best fitting pendent in the Carbon 
Aware Computing API. For detailed mapping, have a look at the [LocationMapper](../../carbon-reductor-core/src/main/java/de/envite/greenbpm/carbonreductor/core/adapter/carbonawarecomputing/LocationMapper.java). 

# Getting Started

## Register for the web API

The registration process is documented in the [carbon-aware-computing GitHub repository](https://github.com/bluehands/Carbon-Aware-Computing#web-api).
