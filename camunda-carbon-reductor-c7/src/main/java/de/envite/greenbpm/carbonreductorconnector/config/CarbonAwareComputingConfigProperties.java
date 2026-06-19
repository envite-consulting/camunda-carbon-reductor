package de.envite.greenbpm.carbonreductorconnector.config;

import static de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.PropertyPrefix.CARBON_AWARE_COMPUTING;
import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.CarbonAwareComputingProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EqualsAndHashCode(callSuper = true)
@Data
@Configuration
@ConfigurationProperties(prefix = CONFIG_PROPERTY_PREFIX + "." + CARBON_AWARE_COMPUTING)
class CarbonAwareComputingConfigProperties extends CarbonAwareComputingProperties {}
