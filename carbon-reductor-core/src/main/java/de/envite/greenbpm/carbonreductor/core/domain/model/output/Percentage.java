package de.envite.greenbpm.carbonreductor.core.domain.model.output;

import io.github.domainprimitives.type.ValueObject;

import static io.github.domainprimitives.validation.Constraints.isNotNullDouble;

public class Percentage extends ValueObject<Double> {
    public Percentage(Double value) {
        super(value, isNotNullDouble());
    }
}
