package de.envite.greenbpm.carbonreductorconnector.config;

import de.envite.greenbpm.carbonreductor.core.domain.service.DelayCalculatorService;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CarbonReductorCoreConfiguration {

  @Bean
  public DelayCalculator delayCalculator(CarbonEmissionQuery carbonEmissionQuery) {
    return new DelayCalculatorService(carbonEmissionQuery);
  }
}
