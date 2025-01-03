package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

@Getter
public class CarbonReduction extends Aggregate {

  private final Delay delay;

  /**
   * CO2eq if executed immediately.
   * Could be null because some data provider only serve the {@link CarbonReduction#optimalForecastedCarbon}.
   */
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
    validateNotNull(optimalForecastedCarbon, "actual Carbon");
    evaluateValidations();
  }

  /**
   * Calculates the Carbon Reduction due to the time shifting
   * @return Carbon Reduction. If no calculation is possible it returns null
   */
  public Carbon calculateReduction() {
    if (carbonWithoutOptimization == null) {
      return null;
    }
    return new Carbon(carbonWithoutOptimization.getValue() - optimalForecastedCarbon.getValue());
  }
}
