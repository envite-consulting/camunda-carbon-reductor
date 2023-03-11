package de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception;

public class CarbonEmissionQueryException extends Exception {

  public CarbonEmissionQueryException(Exception e) {
    super(e);
  }

  public CarbonEmissionQueryException(String message) {
    super(message);
  }

  public CarbonEmissionQueryException(String message, Exception e) {
    super(message, e);
  }
}
