package de.envite.greenbpm.carbonreductorconnector.domain.model.input;

import io.github.domainprimitives.type.ValueObject;
import io.github.domainprimitives.validation.Validation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class Duration extends ValueObject<String> {
    public Duration(String value) {
        super(value, isValidDuration());
    }

    private static Consumer<Validation<String>> isValidDuration() {
        return (val) ->
                val.constraint(val.value() != null && validDuration(val.value()),
                    () -> String.format("%s is not a valid ISO 8601 duration", val.value()));
    }

    private static boolean validDuration(String value) {
        boolean valid = true;
        try {
            java.time.Duration.parse(value);
        } catch (DateTimeParseException e) {
            valid = false;
        }
        return valid;
    }

    public OffsetDateTime asDate() {
        long timerDurationMillis = this.asDuration();
        return OffsetDateTime.now(ZoneOffset.UTC).plus(timerDurationMillis, ChronoUnit.MILLIS);
    }

    public long asDuration() {
        java.time.Duration duration = java.time.Duration.parse(this.getValue());
        return duration.toMillis();
    }

    public int asDurationInMinutes() {
        return (int) Math.ceil((double) (asDuration() / 1000) / 60);
    }
}
