package de.envite.greenbpm.carbonreductor.core.adapter.watttime.config;

import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiClient;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.CarbonAwareApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class CarbonAwareSdkClientConfiguration {

  @Bean
  public CarbonAwareApi carbonAwareApi(CarbonAwareClientProperties carbonAwareClientProperties) {
    log.debug("Creating api client for carbon aware api with base path '{}'", carbonAwareClientProperties.getBasePath());
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(carbonAwareClientProperties.getBasePath());
    return new CarbonAwareApi(apiClient);
  }
}
