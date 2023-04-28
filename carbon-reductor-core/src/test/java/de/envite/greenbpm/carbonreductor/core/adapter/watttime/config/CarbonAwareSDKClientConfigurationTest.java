package de.envite.greenbpm.carbonreductor.core.adapter.watttime.config;

import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarbonAwareSDKClientConfigurationTest {

    private final CarbonAwareSdkClientConfiguration classUnderTest = new CarbonAwareSdkClientConfiguration();

    @Test
    void should_create_api_and_set_base_path_of_property() {
        final String basePath = "foo.bar:90";
        CarbonAwareClientProperties properties = new CarbonAwareClientProperties();
        properties.setBasePath(basePath);

        CarbonAwareApi result = classUnderTest.carbonAwareApi(properties);

        assertThat(result).isNotNull();
        assertThat(result.getApiClient().getBasePath()).isEqualTo(basePath);
    }
}