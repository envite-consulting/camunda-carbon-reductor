package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import static org.assertj.core.api.Assertions.assertThat;

import de.envite.greenbpm.api.carbonawarecomputing.ApiClient;
import de.envite.greenbpm.api.carbonawarecomputing.auth.ApiKeyAuth;
import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.CarbonAwareComputingProperties;
import org.junit.jupiter.api.Test;

class CarbonAwareComputingApiClientFactoryTest {

  @Test
  void should_create_api_client_and_set_base_path_and_api_key_of_properties() {
    final String basePath = "foo.bar:90";
    final String apiKey = "secret-api-key";
    CarbonAwareComputingProperties properties = new CarbonAwareComputingProperties();
    properties.setBasePath(basePath);
    properties.setApiKey(apiKey);

    ApiClient result =
        CarbonAwareComputingApiClientFactory.carbonAwareComputingApiClient(properties);

    assertThat(result).isNotNull();
    assertThat(result.getBasePath()).isEqualTo(basePath);
    assertThat(((ApiKeyAuth) result.getAuthentication("apikey")).getApiKey()).isEqualTo(apiKey);
  }
}
