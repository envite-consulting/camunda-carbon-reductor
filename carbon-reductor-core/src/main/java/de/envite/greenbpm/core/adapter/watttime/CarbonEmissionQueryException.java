package de.envite.greenbpm.core.adapter.watttime;

public class CarbonEmissionQueryException extends Exception {

  public CarbonEmissionQueryException(Exception e) {
    super(e);
  }
  public CarbonEmissionQueryException(String message, Exception e) {
    super(message, e);
  }
}
