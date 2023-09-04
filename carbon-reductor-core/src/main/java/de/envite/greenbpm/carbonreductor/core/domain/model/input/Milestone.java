package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.github.domainprimitives.type.ValueObject;

import java.time.OffsetDateTime;
import java.time.temporal.Temporal;

import static io.github.domainprimitives.validation.Constraints.isInPast;

public class Milestone extends ValueObject<Temporal> {

    public Milestone(OffsetDateTime value) {
        super(value, isInPast());
    }

    public OffsetDateTime asDate() {
        return (OffsetDateTime) getValue();
    }
}
