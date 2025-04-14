package de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe;

import io.github.domainprimitives.validation.InvariantException;
import io.skippy.junit5.PredictWithSkippy;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@PredictWithSkippy
class OptimalTimeTest {

    private final OffsetDateTime utcNow = OffsetDateTime.now(ZoneOffset.UTC);

    @Test
    void should_not_throw_if_valid() {
        assertDoesNotThrow(() -> new OptimalTime(utcNow));
    }

    @Nested
    class ConvertToOffsetDateTime {

        @Test
        void should_convert() {
            final OptimalTime optimalTime = new OptimalTime(utcNow);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(optimalTime.asOffsetDateTime()).isEqualTo(utcNow);
            softAssertions.assertThat(optimalTime.asOffsetDateTime()).isInstanceOf(OffsetDateTime.class);
            softAssertions.assertAll();
        }
    }

    @Nested
    class IsInFuture {

        @Test
        void should_return_true_if_is_in_future() {
            final OptimalTime nextHour = new OptimalTime(utcNow.plusHours(1));

            assertThat(nextHour.isInFuture()).isTrue();
        }

        @Test
        void should_return_true_if_is_in_now() {
            final OptimalTime now = new OptimalTime(utcNow);

            assertThat(now.isInFuture()).isTrue();
        }

        @Test
        void should_return_false_if_is_in_past() {
            final OptimalTime lastHour = new OptimalTime(utcNow.minusHours(1));

            assertThat(lastHour.isInFuture()).isFalse();
        }
    }

    @Nested
    class Invariant {
        @Test
        void should_throw_if_is_null() {
            assertThatThrownBy(() -> new OptimalTime(null))
                    .isInstanceOf(InvariantException.class)
                    .hasMessage("Value(s) of OptimalTime is not valid: OptimalTime should not be null.");
        }
    }
}