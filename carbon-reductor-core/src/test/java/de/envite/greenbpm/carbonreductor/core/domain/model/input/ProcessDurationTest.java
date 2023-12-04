package de.envite.greenbpm.carbonreductor.core.domain.model.input;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessDurationTest {

    @Nested
    class Invariants {

        @Test
        void should_create_with_valid_duration() {
            final Duration duration = Duration.parse("PT5M");

            ProcessDuration processDuration = new ProcessDuration(duration);

            assertThat(processDuration).isNotNull();
            assertThat(processDuration.getValue()).isEqualTo(duration);
        }

        @Test
        void should_create_with_valid_string() {
            final String durationString = "PT5M";
            final Duration duration = Duration.parse(durationString);

            ProcessDuration processDuration = new ProcessDuration(durationString);

            assertThat(processDuration).isNotNull();
            assertThat(processDuration.getValue()).isEqualTo(duration);
        }

        @Test
        void should_not_create_with_empty_string() {
            assertThatThrownBy(() -> new ProcessDuration(""))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }

    @Test
    void should_get_duration_in_minutes() {
        final Duration duration = Duration.parse("PT120S");

        ProcessDuration processDuration = new ProcessDuration(duration);

        assertThat(processDuration).isNotNull();
        assertThat(processDuration.inMinutes()).isEqualTo(2);
    }
}