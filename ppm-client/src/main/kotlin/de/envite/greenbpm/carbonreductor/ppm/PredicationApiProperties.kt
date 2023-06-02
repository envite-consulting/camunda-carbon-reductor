package de.envite.greenbpm.carbonreductor.ppm

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "carbon-reductor.ppm.predication-api")
data class PredicationApiProperties(
    var baseURL: String = "",
)
