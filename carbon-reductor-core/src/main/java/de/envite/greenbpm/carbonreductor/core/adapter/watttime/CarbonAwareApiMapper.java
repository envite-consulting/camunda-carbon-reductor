package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsDataDTO;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.util.Optional.of;

@Component
public class CarbonAwareApiMapper {

  public EmissionTimeframe mapToDomain(EmissionsForecastDTO emissionsForecast) {
    Optional<EmissionsDataDTO> optimalDataPoint = of(emissionsForecast)
            .map(EmissionsForecastDTO::getOptimalDataPoints)
            .map(d -> d.get(0));
    OptimalTime optimalTime = optimalDataPoint
            .map(EmissionsDataDTO::getTimestamp)
            .map(org.threeten.bp.OffsetDateTime::toString)
            .map(OffsetDateTime::parse)
            .map(OptimalTime::new)
            .orElse(null);
    ForecastedValue forecastedValue = optimalDataPoint
            .map(EmissionsDataDTO::getValue)
            .map(ForecastedValue::new)
            .orElse(null);

    Rating rating = of(emissionsForecast)
            .map(EmissionsForecastDTO::getForecastData)
            .map(d -> d.get(0))
            .map(EmissionsDataDTO::getValue)
            .map(Rating::new)
            .orElse(null);

    return new EmissionTimeframe(optimalTime, rating, forecastedValue);
  }
}
