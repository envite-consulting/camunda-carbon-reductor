package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.api.carbonawaresdk.ApiClient;
import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.config.CarbonAwareClientProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CarbonAwareApiFactory {

  public static CarbonAwareApi carbonAwareApi(CarbonAwareClientProperties properties) {
    ApiClient client = new ApiClient();
    client.setBasePath(properties.getBasePath());
    return new CarbonAwareApi(client);
  }
}
