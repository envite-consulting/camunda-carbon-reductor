package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.api.carbonawaresdk.ApiResponse;
import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class CarbonAwareSdkClient implements CarbonEmissionQuery {

  private final CarbonAwareApi carbonAwareApi;
  private final CarbonAwareApiMapper carbonAwareApiMapper;

  @Override
  public EmissionTimeframe getEmissionTimeframe(Location location, ProcessDuration processDuration, ProcessDuration executiontime) throws CarbonEmissionQueryException {
    EmissionsForecastDTO emissionsForecast = getOptimalForecastUntil(location, processDuration, executiontime);
    return carbonAwareApiMapper.mapToDomain(emissionsForecast);
  }
  private EmissionsForecastDTO getOptimalForecastUntil(Location location, ProcessDuration processDuration, ProcessDuration executiontime)
          throws CarbonEmissionQueryException {
    ApiResponse<List<EmissionsForecastDTO>> currentForecastDataWithHttpInfo;
    OffsetDateTime offsetDateTime = OffsetDateTime.parse(processDuration.timeshiftFromNow().toString());
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
            .filter(d -> !d.isEmpty())
            .map(d -> d.get(0))
            .orElseThrow(() -> new CarbonEmissionQueryException("API provided no data"));
  }
}
