package de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe;

import io.github.domainprimitives.type.ValueObject;
import io.github.domainprimitives.validation.Validation;

import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.function.Consumer;


public class OptimalTime extends ValueObject<Temporal> {

    private static Consumer<Validation<Temporal>> isNotNull() {
        return (val) -> val.constraint(val.value() != null, () -> "should not be null");
    }

    public OptimalTime(OffsetDateTime value) {
        super(value, isNotNull());
    }

    public OffsetDateTime asOffsetDateTime() {
        return ((OffsetDateTime)getValue());
    }

    public boolean isInFuture() {
        // TODO: Better Solution to handle "now == now"?
        return this.asOffsetDateTime().isAfter(OffsetDateTime.now().minusMinutes(1));
    }
}
