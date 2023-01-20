package de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe;

import io.github.domainprimitives.type.ValueObject;

import java.time.OffsetDateTime;
import java.time.temporal.Temporal;

import static io.github.domainprimitives.validation.Constraints.isInFuture;

public class OptimalTime extends ValueObject<Temporal> {
    public OptimalTime(OffsetDateTime value) {
        super(value);
    }

    public OffsetDateTime asOffsetDateTime() {
        return ((OffsetDateTime)getValue());
    }
}
