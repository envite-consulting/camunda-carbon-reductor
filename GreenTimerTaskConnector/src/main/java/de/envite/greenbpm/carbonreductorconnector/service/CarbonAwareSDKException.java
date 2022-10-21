package de.envite.greenbpm.carbonreductorconnector.service;

import io.swagger.client.ApiException;

public class CarbonAwareSDKException extends Exception {

  public CarbonAwareSDKException(ApiException apiException) {
    this(
        "Error when calling the carbonawaresdk. Returned "
            + apiException.getCode()
            + " "
            + apiException.getResponseBody(),
        apiException);
  }

  public CarbonAwareSDKException() {
    super();
  }

  public CarbonAwareSDKException(String message) {
    super(message);
  }

  public CarbonAwareSDKException(Throwable cause) {
    super(cause);
  }

  public CarbonAwareSDKException(String message, Throwable cause) {
    super(message, cause);
  }
}
