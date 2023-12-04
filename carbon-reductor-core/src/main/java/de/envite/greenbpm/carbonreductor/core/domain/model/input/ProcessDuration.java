package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.github.domainprimitives.type.ValueObject;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@EqualsAndHashCode(callSuper = false)
public class ProcessDuration extends ValueObject<Duration> {
    public ProcessDuration(Duration value) {
        super(value);
    }

    public ProcessDuration(String value) {
        this(Duration.parse(value));
    }

    // TODO: Move to Service and inject clock
    public OffsetDateTime timeshiftFromNow() {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(getValue());
    }

    public int inMinutes() {
        return (int) (getValue().getSeconds() / 60);
    }
}
