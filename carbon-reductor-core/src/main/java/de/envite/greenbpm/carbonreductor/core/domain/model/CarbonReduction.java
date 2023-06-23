package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

@Getter
public class CarbonReduction extends Aggregate {

  private final Delay delay;

  private final Carbon carbonWithoutOptimization;
  private final Carbon optimalForecastedCarbon;
  private final Percentage savedCarbonPercentage;

  public CarbonReduction(Delay delay, Carbon carbonWithoutOptimization, Carbon optimalForcatedCarbon, Percentage savedCarbonPercentage) {
    this.delay = delay;
    this.carbonWithoutOptimization = carbonWithoutOptimization;
    this.optimalForecastedCarbon = optimalForcatedCarbon;
    this.savedCarbonPercentage = savedCarbonPercentage;
    this.validate();
  }

  @Override
  protected void validate() {
    validateNotNull(delay, "Delay");
    validateNotNull(carbonWithoutOptimization, "original Carbon");
    validateNotNull(optimalForecastedCarbon, "actual Carbon");
    validateNotNull(savedCarbonPercentage, "saved Carbon");
    evaluateValidations();
  }

  public Carbon calculateReduction() {
    return new Carbon(carbonWithoutOptimization.getValue() - optimalForecastedCarbon.getValue());
  }
}
