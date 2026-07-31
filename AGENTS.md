---
name: Camunda Carbon Reductor
description: The Camunda Carbon Reductor is an Service Task Template for Camunda 8 (and 7) that allows you to time shift your processes' carbon emissions when energy is clean while still fulfilling the requested SLAs.
---

# Camunda Carbon Reductor

## Project Overview

The Camunda Carbon Reductor allows you to time shift your processes' carbon emissions when energy is clean while still fulfilling the requested SLAs.

Technically, it's implemented as a Camunda [Connector](https://docs.camunda.io/docs/components/connectors/introduction-to-connectors/) for Camunda Platform 8 and as an [External Task Worker](https://docs.camunda.org/manual/latest/user-guide/process-engine/external-tasks/) for Camunda Platform 7.

## Project Structure

Multi-Module Maven Project:
- `/carbon-reductor-core`: Core module which calculates the possible time shifting based on data it fetches from the external systems. Centrilized to be reused.
- `/api`: Wrapes external APIs to fetch carbon aware data.
  - `/api/api-carbon-aware`: Custom Client to connect to the [Carbon Aware SDK](https://github.com/Green-Software-Foundation/carbon-aware-sdk)
  - `/api/api-carbon-aware-computing`: Custom Client to connect to the [Carbon Aware Computing](https://www.carbon-aware-computing.com/) API
- `/camunda-carbon-reductor-c8`: Camunda 8 Job Worker
- `/camunda-carbon-reductor-c7`: Camunda 7 External Task

## Architecture

`/carbon-reductor-core`:
- Clean Architecture: Domain (Model + Service) <- Use Case -> Adapter
  - No Infrastructure in der Domain, excepted Dependency Injection mechanism
- Building blocks from tactical DDD:
  - Immutable domain models (value objects, entities, aggregates) that represent your invariants and thus validate themselves.
  - Domain events to change the state of the domain.
  - Domain services for complex operations and cross-domain validations.

`/camunda-carbon-reductor-c8`: and `/camunda-carbon-reductor-c7` are simple, lean adapters which use the `/carbon-reductor-core`

## Quality Assurance

- Compile: `./mvnw compile`
- Test: `./mvnw test`

## Constraints

- Keep commits small and manageable.
- Every commit must compile, the tests must pass, and the documentation must be up-to-date and consistent.
- The README.md must be kept up-to-date.
- For detailed guidelines on contributing to the project, please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file.
