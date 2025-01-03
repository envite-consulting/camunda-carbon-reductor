package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import lombok.Data;

@Data
public class CarbonReductorOutputVariable {

  private boolean executionDelayed;
  private Double carbonWithoutOptimization;
  private double optimalForecastedCarbon;
  private double savedCarbonPercentage;
  private Double carbonReduction;
  private long delayedBy;

}
