package de.envite.greenbpm.carbonreductor.core.adapter.watttime;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiClient;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiResponse;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsDataDTO;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class CarbonAwareSDKService implements CarbonEmissionQuery {

  public static final String HTTP_LOCALHOST = "http://localhost:8090";
  private final CarbonAwareApi client;

  public CarbonAwareSDKService() {
    ApiClient apiClient = new ApiClient();
    final String CARBON_AWARE_BASEPATH = System.getenv("CARBON_AWARE_BASEPATH");
    if (CARBON_AWARE_BASEPATH != null && !CARBON_AWARE_BASEPATH.isEmpty()) {
      apiClient.setBasePath(CARBON_AWARE_BASEPATH);
    } else {
      apiClient.setBasePath(HTTP_LOCALHOST);
    }
    client = new CarbonAwareApi(apiClient);
  }

  @Override
  public EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException {
    EmissionsForecastDTO emissionsForecast = getOptimalForecastUntil(location.getValue(), timeshift.timeshiftFromNow(), executiontime.getValue());
    EmissionsDataDTO optimalDataPoint = of(emissionsForecast)
            .map(EmissionsForecastDTO::getOptimalDataPoints)
            .map(d -> d.get(0)).orElseThrow(() -> new CarbonEmissionQueryException("API does not provide any optimal data points"));
    EmissionsDataDTO currentEmission = of(emissionsForecast)
            .map(EmissionsForecastDTO::getForecastData)
            .map(d -> d.get(0)).orElseThrow(() -> new CarbonEmissionQueryException("API does not provide any forecast data"));

    return new EmissionTimeframe(
            new OptimalTime(OffsetDateTime.parse(optimalDataPoint.getTimestamp().toString())),
            new Rating(currentEmission.getValue()),
            new ForecastedValue(optimalDataPoint.getValue())
    );
  }
  private EmissionsForecastDTO getOptimalForecastUntil(String location, OffsetDateTime until, Duration windowsize)
          throws CarbonEmissionQueryException {
    ApiResponse<List<EmissionsForecastDTO>> currentForecastDataWithHttpInfo;
    int windowsizeInMin = (int) (windowsize.getSeconds() / 60);
    try {
      currentForecastDataWithHttpInfo =
              client.getCurrentForecastDataWithHttpInfo(
                      List.of(location), null, org.threeten.bp.OffsetDateTime.parse(until.toString()), windowsizeInMin);
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
