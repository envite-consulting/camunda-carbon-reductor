package de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe;

import io.github.domainprimitives.type.ValueObject;

import static io.github.domainprimitives.validation.Constraints.isNotNullDouble;

public class Rating extends ValueObject<Double> {
    public Rating(Double value) {
        super(value, isNotNullDouble());
    }
}
