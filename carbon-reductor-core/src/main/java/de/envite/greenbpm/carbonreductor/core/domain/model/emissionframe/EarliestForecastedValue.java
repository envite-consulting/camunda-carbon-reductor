package de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe;

import io.github.domainprimitives.type.ValueObject;

import static io.github.domainprimitives.validation.Constraints.isNotNullDouble;

public class EarliestForecastedValue extends ValueObject<Double> {
    public EarliestForecastedValue(Double value) {
        super(value, isNotNullDouble());
    }
}
