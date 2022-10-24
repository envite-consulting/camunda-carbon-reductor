package de.envite.greenbpm.carbonreductorconnector.service;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.CarbonAwareApi;
import io.swagger.client.model.EmissionsData;
import io.swagger.client.model.EmissionsDataDTO;
import io.swagger.client.model.EmissionsForecastDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

@Slf4j
public class CarbonAwareSDKService {

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

  public EmissionsData getCurrentEmission(Locations location) throws CarbonAwareSDKException {
    ApiResponse<List<EmissionsData>> emissionsDataForLocationByTimeWithHttpInfo = null;
    try {
      emissionsDataForLocationByTimeWithHttpInfo =
          client.getEmissionsDataForLocationByTimeWithHttpInfo(
              location.regionname(),
              OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(10),
              OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(5));
    } catch (ApiException e) {
      log.error(
          "Error when calling the CarbonAwareSDK for the currentEmission: {}",
          e.getResponseBody(),
          e);
      throw new CarbonAwareSDKException(e);
    }
    return emissionsDataForLocationByTimeWithHttpInfo.getData().get(0);
  }

  public EmissionsDataDTO getOptimalForecastUntil(Locations location, OffsetDateTime until)
      throws CarbonAwareSDKException {
    ApiResponse<List<EmissionsForecastDTO>> currentForecastDataWithHttpInfo = null;
    try {
      currentForecastDataWithHttpInfo =
          client.getCurrentForecastDataWithHttpInfo(
              List.of(location.regionname()), null, until, null);
    } catch (ApiException e) {
      log.error(
          "Error when calling the CarbonAwareSDK for the optimalForecastUntil: {}",
          e.getResponseBody(),
          e);
      throw new CarbonAwareSDKException(e);
    }
    return currentForecastDataWithHttpInfo.getData().get(0).getOptimalDataPoints().get(0);
  }
}
