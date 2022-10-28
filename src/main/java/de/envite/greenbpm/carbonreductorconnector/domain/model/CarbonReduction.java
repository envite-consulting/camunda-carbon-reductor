package de.envite.greenbpm.carbonreductorconnector.domain.model;

import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Delay;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

@Getter
public class CarbonReduction extends Aggregate {

  private final Delay delay;
  private final Carbon originalCarbon;
  private final Carbon actualCarbon;
  private final Carbon savedCarbon;

  public CarbonReduction(Delay delay, Carbon originalCarbon, Carbon actualCarbon, Carbon savedCarbon) {
    this.delay = delay;
    this.originalCarbon = originalCarbon;
    this.actualCarbon = actualCarbon;
    this.savedCarbon = savedCarbon;
    this.validate();
  }

  @Override
  protected void validate() {
    validateNotNull(delay, "Delay");
    validateNotNull(originalCarbon, "original Carbon");
    validateNotNull(actualCarbon, "actual Carbon");
    validateNotNull(savedCarbon, "saved Carbon");
    evaluateValidations();
  }

  public Carbon calculateReduction() {
    return new Carbon(originalCarbon.getValue() - actualCarbon.getValue());
  }
}
