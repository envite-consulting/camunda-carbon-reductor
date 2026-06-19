package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import static org.assertj.core.api.Assertions.assertThat;

import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.config.CarbonAwareClientProperties;
import org.junit.jupiter.api.Test;

class CarbonAwareApiFactoryTest {

  @Test
  void should_create_api_and_set_base_path_of_property() {
    final String basePath = "foo.bar:90";
    CarbonAwareClientProperties properties = new CarbonAwareClientProperties();
    properties.setBasePath(basePath);

    CarbonAwareApi result = CarbonAwareApiFactory.carbonAwareApi(properties);

    assertThat(result).isNotNull();
    assertThat(result.getApiClient().getBasePath()).isEqualTo(basePath);
  }
}
