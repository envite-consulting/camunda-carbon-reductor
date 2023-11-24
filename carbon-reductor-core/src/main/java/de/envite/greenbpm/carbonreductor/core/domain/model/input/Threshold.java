package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.github.domainprimitives.object.ComposedValueObject;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Threshold extends ComposedValueObject {

    private final boolean enabled;
    private final Float value;

    public Threshold(boolean enabled, Float value) {
        this.enabled = enabled;
        this.value = value;
        validate();
    }

    @Override
    protected void validate() {
        if (enabled && value < 1) {
            addInvariantViolation("The Threshold must be greater than 1");
        }
        evaluateValidations();
    }

    public boolean isGreaterThanMinimumThreshold(final Double savedCarbonDelta) {
     return !enabled || value <= savedCarbonDelta;
    }
}
