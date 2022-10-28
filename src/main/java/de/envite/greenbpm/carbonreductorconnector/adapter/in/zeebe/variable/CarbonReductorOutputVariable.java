package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import lombok.Data;

@Data
public class CarbonReductorOutputVariable {

  private boolean executionDelayed;
  private double originalCarbon;
  private double actualCarbon;
  private double savedCarbon;
  private long delayedBy;

}
