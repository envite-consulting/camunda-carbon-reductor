package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.config;

import static de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.PropertyPrefix.CARBON_AWARE_COMPUTING;
import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = CONFIG_PROPERTY_PREFIX + "." + CARBON_AWARE_COMPUTING)
class CarbonAwareComputingProperties {

  private String basePath;
  private String apiKey;
}
