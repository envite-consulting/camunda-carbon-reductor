package de.envite.greenbpm.carbonreductorconnector.domain.model.output;

import io.github.domainprimitives.type.ValueObject;

import static io.github.domainprimitives.validation.Constraints.isNotNullDouble;

public class Carbon extends ValueObject<Double> {
    public Carbon(Double value) {
        super(value, isNotNullDouble());
    }
}
