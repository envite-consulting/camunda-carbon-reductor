package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsDataDTO;
import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.util.Optional.of;

public class CarbonAwareApiMapper {

  public EmissionTimeframe mapToDomain(EmissionsForecastDTO emissionsForecast) {
    Optional<EmissionsDataDTO> optimalDataPoint = of(emissionsForecast)
            .map(EmissionsForecastDTO::getOptimalDataPoints)
            .map(d -> d.get(0));
    OptimalTime optimalTime = optimalDataPoint
            .map(EmissionsDataDTO::getTimestamp)
            .map(OffsetDateTime::toString)
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
