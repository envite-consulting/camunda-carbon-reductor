package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config;

import de.envite.greenbpm.api.carbonawarecomputing.ApiClient;
import de.envite.greenbpm.api.carbonawarecomputing.api.ForecastApi;
import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.CarbonAwareComputingApiClient;
import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.CarbonAwareComputingMapper;
import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.LocationMapper;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.config.PropertyPrefix.CARBON_AWARE_COMPUTING;
import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;



@Configuration
@ConditionalOnProperty(prefix = CONFIG_PROPERTY_PREFIX, name = CARBON_AWARE_COMPUTING + ".enabled")
public class CarbonAwareComputingClientConfiguration {

    @Bean
    public ApiClient carbonAwareComputingApiClient(CarbonAwareComputingProperties properties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBasePath());
        apiClient.setApiKey(properties.getApiKey());
        return apiClient;
    }

    @Bean
    public ForecastApi forecastApi(ApiClient apiClient) {
        return new ForecastApi(apiClient);
    }

    @Bean
    public LocationMapper locationMapper() {
        return new LocationMapper();
    }

    @Bean
    public CarbonAwareComputingMapper carbonAwareComputingMapper() {
        return new CarbonAwareComputingMapper();
    }

    @Bean
    public CarbonEmissionQuery carbonEmissionQueryByAwareComputing(LocationMapper locationMapper, CarbonAwareComputingMapper carbonAwareComputingMapper, ForecastApi forecastApi) {
        return new CarbonAwareComputingApiClient(forecastApi, locationMapper, carbonAwareComputingMapper);
    }

}
