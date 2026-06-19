package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.config;

import static de.envite.greenbpm.carbonreductor.core.adapter.watttime.config.PropertyPrefix.CARBON_AWARE_API;
import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

import de.envite.greenbpm.carbonreductor.core.adapter.watttime.config.CarbonAwareClientProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EqualsAndHashCode(callSuper = true)
@Data
@Configuration
@ConfigurationProperties(prefix = CONFIG_PROPERTY_PREFIX + "." + CARBON_AWARE_API)
class CarbonAwareClientConfigProperties extends CarbonAwareClientProperties {}
