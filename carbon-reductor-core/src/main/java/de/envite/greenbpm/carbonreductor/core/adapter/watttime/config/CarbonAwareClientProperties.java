package de.envite.greenbpm.carbonreductor.core.adapter.watttime.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

@Data
@Configuration
@ConfigurationProperties(prefix = CONFIG_PROPERTY_PREFIX + ".carbon-aware-api")
class CarbonAwareClientProperties {

  private String basePath;
}
