package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.config;

import static de.envite.greenbpm.carbonreductor.core.adapter.watttime.config.PropertyPrefix.CARBON_AWARE_API;
import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

import de.envite.greenbpm.api.carbonawaresdk.ApiClient;
import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareApiMapper;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareSdkClient;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(
    prefix = CONFIG_PROPERTY_PREFIX,
    name = CARBON_AWARE_API + ".enabled",
    matchIfMissing = true)
class CarbonAwareSdkClientConfiguration {

  @Bean
  public CarbonAwareApi carbonAwareApi(CarbonAwareClientProperties carbonAwareClientProperties) {
    log.debug(
        "Creating api client for carbon aware api with base path '{}'",
        carbonAwareClientProperties.getBasePath());
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(carbonAwareClientProperties.getBasePath());
    return new CarbonAwareApi(apiClient);
  }

  @Bean
  public CarbonAwareApiMapper carbonAwareApiMapper() {
    return new CarbonAwareApiMapper();
  }

  @Bean
  public CarbonEmissionQuery carbonEmissionQueryByAwareSdk(
      CarbonAwareApiMapper carbonAwareApiMapper, CarbonAwareApi carbonAwareApi) {
    return new CarbonAwareSdkClient(carbonAwareApi, carbonAwareApiMapper);
  }
}
