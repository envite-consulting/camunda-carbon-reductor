package de.envite.greenbpm.core.domain.model.input;

import io.github.domainprimitives.type.ValueObject;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@EqualsAndHashCode(callSuper = false)
public class Timeshift extends ValueObject<Duration> {
    public Timeshift(Duration value) {
        super(value);
    }

    public Timeshift(String value) {
        this(Duration.parse(value));
    }

    public OffsetDateTime timeshiftFromNow() {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(getValue());
    }
}
