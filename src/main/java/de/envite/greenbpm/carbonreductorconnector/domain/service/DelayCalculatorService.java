package de.envite.greenbpm.carbonreductorconnector.domain.service;

import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductorconnector.usecase.out.CarbonEmissionQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelayCalculatorService implements DelayCalculator {
  private final CarbonEmissionQuery carbonEmissionQuery;

  @Override
  public CarbonReductorOutput calculateDelay(CarbonReductorInput input) throws CarbonReductorException {

    EmissionTimeframe emissionTimeframe = null;
    try {
      emissionTimeframe = carbonEmissionQuery.getCurrentEmission(input.getLocation(), input.getDuration());
    } catch (CarbonEmissionQueryException e) {
      throw new CarbonReductorException("Could not query API to get infos about future emissions", e);
    }

    boolean isDelayNecessary = input.isDelayStillRelevant() && emissionTimeframe.isCleanerEnergyInFuture();

    if (isDelayNecessary) {
      final long optimalTime = emissionTimeframe.getOptimalTime().asOffsetDateTime().toInstant().toEpochMilli();
      final long delayedBy = optimalTime - OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
      return CarbonReductorOutput.builder()
          .executionDelayed(true)
          .delayedBy(delayedBy)
          .originalCarbon(emissionTimeframe.getRating().getValue())
          .actualCarbon(emissionTimeframe.getForecastedValue().getValue())
          .savedCarbon(emissionTimeframe.calculateSavedCarbonPercentage())
          .build();
    }
    // execution is optimal currently
    return CarbonReductorOutput.builder()
        .executionDelayed(false)
        .delayedBy(0)
        .originalCarbon(emissionTimeframe.getRating().getValue())
        .actualCarbon(emissionTimeframe.getRating().getValue())
        .savedCarbon(0.0)
        .build();
  }
}
