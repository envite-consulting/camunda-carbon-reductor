package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.config;

import static org.assertj.core.api.Assertions.assertThat;

import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.CarbonAwareComputingApiClient;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareSdkClient;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class CarbonReductorWiringTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
          .withUserConfiguration(
              CarbonReductorCoreConfiguration.class,
              CarbonAwareSdkClientConfiguration.class,
              CarbonAwareComputingClientConfiguration.class,
              CarbonAwareClientConfigProperties.class,
              CarbonAwareComputingConfigProperties.class);

  @Test
  void defaults_to_carbon_aware_sdk() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(DelayCalculator.class);
          assertThat(context.getBean(CarbonEmissionQuery.class))
              .isInstanceOf(CarbonAwareSdkClient.class);
          assertThat(context).doesNotHaveBean(CarbonAwareComputingApiClient.class);
        });
  }

  @Test
  void uses_carbon_aware_computing_when_enabled() {
    contextRunner
        .withPropertyValues(
            "carbon-reductor.carbon-aware-computing.enabled=true",
            "carbon-reductor.carbon-aware-api.enabled=false")
        .run(
            context -> {
              assertThat(context).hasSingleBean(DelayCalculator.class);
              assertThat(context.getBean(CarbonEmissionQuery.class))
                  .isInstanceOf(CarbonAwareComputingApiClient.class);
              assertThat(context).doesNotHaveBean(CarbonAwareSdkClient.class);
            });
  }
}
