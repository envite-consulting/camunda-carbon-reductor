package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import lombok.Data;

@Data
public class CarbonReductorOutputVariable {

  private boolean executionDelayed;
  private Double carbonWithoutOptimization;
  private double optimalForecastedCarbon;
  private Double savedCarbonPercentage;
  private Double carbonReduction;
  private long delayedBy;

}
