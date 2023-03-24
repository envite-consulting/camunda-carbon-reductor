package de.envite.greenbpm.carbonreductor.core.domain.model.input;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeshiftTest {

    @Nested
    class Invariants {

        @Test
        void should_create_with_valid_duration() {
            final Duration duration = Duration.parse("PT5M");

            Timeshift timeshift = new Timeshift(duration);

            assertThat(timeshift).isNotNull();
            assertThat(timeshift.getValue()).isEqualTo(duration);
        }

        @Test
        void should_create_with_valid_string() {
            final String durationString = "PT5M";
            final Duration duration = Duration.parse(durationString);

            Timeshift timeshift = new Timeshift(durationString);

            assertThat(timeshift).isNotNull();
            assertThat(timeshift.getValue()).isEqualTo(duration);
        }

        @Test
        void should_not_create_with_empty_string() {
            assertThatThrownBy(() -> new Timeshift(""))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }

    @Test
    void should_get_duration_in_minutes() {
        final Duration duration = Duration.parse("PT120S");

        Timeshift timeshift = new Timeshift(duration);

        assertThat(timeshift).isNotNull();
        assertThat(timeshift.inMinutes()).isEqualTo(2);
    }
}