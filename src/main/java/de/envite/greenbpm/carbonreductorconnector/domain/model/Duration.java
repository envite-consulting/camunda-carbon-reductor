package de.envite.greenbpm.carbonreductorconnector.domain.model;

import io.github.domainprimitives.type.ValueObject;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Duration extends ValueObject<String> {
    public Duration(String value) {
        // TODO: Add regex Pattern for Validation: isPattern(...)
        super(value);
    }

    public OffsetDateTime asDate() {
        long timerDurationMillis = this.asDuration();
        return OffsetDateTime.now(ZoneOffset.UTC).plus(timerDurationMillis, ChronoUnit.MILLIS);
    }

    public long asDuration() {
        java.time.Duration duration = java.time.Duration.parse(this.getValue());
        return duration.toMillis();
    }
}
