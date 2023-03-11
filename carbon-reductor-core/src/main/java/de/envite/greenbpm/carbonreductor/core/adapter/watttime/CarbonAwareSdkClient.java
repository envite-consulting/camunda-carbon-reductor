package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiResponse;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarbonAwareSdkClient implements CarbonEmissionQuery {

  private final CarbonAwareApi carbonAwareApi;
  private final CarbonAwareApiMapper carbonAwareApiMapper;

  @Override
  public EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException {
    EmissionsForecastDTO emissionsForecast = getOptimalForecastUntil(location, timeshift, executiontime);
    return carbonAwareApiMapper.mapToDomain(emissionsForecast);
  }
  private EmissionsForecastDTO getOptimalForecastUntil(Location location, Timeshift timeshift, Timeshift executiontime)
          throws CarbonEmissionQueryException {
    ApiResponse<List<EmissionsForecastDTO>> currentForecastDataWithHttpInfo;
    org.threeten.bp.OffsetDateTime offsetDateTime = org.threeten.bp.OffsetDateTime.parse(timeshift.timeshiftFromNow().toString());
    try {
      currentForecastDataWithHttpInfo =
              carbonAwareApi.getCurrentForecastDataWithHttpInfo(
                      List.of(location.getValue()), null, offsetDateTime, executiontime.inMinutes());
    } catch (Exception e) {
      log.error("Error when calling the API for the optimalForecastUntil", e);
      throw new CarbonEmissionQueryException(e);
    }
    return ofNullable(currentForecastDataWithHttpInfo)
            .map(ApiResponse::getData)
            .map(d -> d.get(0))
            .orElseThrow(() -> new CarbonEmissionQueryException("API provided no data"));
  }
}
