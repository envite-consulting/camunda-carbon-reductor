package de.envite.greenbpm.carbonreductorconnector

import de.envite.greenbpm.carbonreductor.core.EnableCarbonReductorCore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableCarbonReductorCore
class CamundaConnectorApplication

fun main(args: Array<String>) {
    runApplication<CamundaConnectorApplication>(*args)
}