package de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime;

import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductorconnector.usecase.out.CarbonEmissionQuery;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.CarbonAwareApi;
import io.swagger.client.model.EmissionsDataDTO;
import io.swagger.client.model.EmissionsForecastDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

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
    EmissionsDataDTO optimalDataPoint = emissionsForecast.getOptimalDataPoints().get(0);
    EmissionsDataDTO currentEmission = emissionsForecast.getForecastData().get(0);

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
    } catch (ApiException e) {
      log.error(
              "Error when calling the CarbonAwareSDK for the optimalForecastUntil: {}",
              e.getResponseBody(),
              e);
      throw new CarbonEmissionQueryException(e);
    }
    return currentForecastDataWithHttpInfo.getData().get(0);//.getOptimalDataPoints().get(0);
  }
}
