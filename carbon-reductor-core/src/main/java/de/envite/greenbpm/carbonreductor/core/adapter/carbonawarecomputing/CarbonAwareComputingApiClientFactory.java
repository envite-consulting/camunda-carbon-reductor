package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.ApiClient;
import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.CarbonAwareComputingProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CarbonAwareComputingApiClientFactory {

  public static ApiClient carbonAwareComputingApiClient(CarbonAwareComputingProperties properties) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(properties.getBasePath());
    apiClient.setApiKey(properties.getApiKey());
    return apiClient;
  }
}
