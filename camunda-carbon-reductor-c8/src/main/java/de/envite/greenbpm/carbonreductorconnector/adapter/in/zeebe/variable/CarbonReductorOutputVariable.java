package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import lombok.Data;

@Data
public class CarbonReductorOutputVariable {

  private boolean executionDelayed;
  private double carbonWithoutOptimization;
  private double optimalForecastedCarbon;
  private double savedCarbonPercentage;
  private double carbonReduction;
  private long delayedBy;

}
