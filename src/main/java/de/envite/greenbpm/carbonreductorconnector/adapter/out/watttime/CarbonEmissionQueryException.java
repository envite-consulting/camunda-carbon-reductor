package de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime;

import io.swagger.client.ApiException;

public class CarbonEmissionQueryException extends Exception {

  public CarbonEmissionQueryException(ApiException apiException) {
    this(
        "Error when calling the carbonawaresdk. Returned "
            + apiException.getCode()
            + " "
            + apiException.getResponseBody(),
        apiException);
  }

  public CarbonEmissionQueryException() {
    super();
  }

  public CarbonEmissionQueryException(String message) {
    super(message);
  }

  public CarbonEmissionQueryException(Throwable cause) {
    super(cause);
  }

  public CarbonEmissionQueryException(String message, Throwable cause) {
    super(message, cause);
  }
}
