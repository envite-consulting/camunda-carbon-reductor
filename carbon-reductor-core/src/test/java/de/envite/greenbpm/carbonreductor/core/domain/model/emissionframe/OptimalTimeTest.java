package de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe;

import io.github.domainprimitives.validation.InvariantException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OptimalTimeTest {


    @Test
    void should_not_throw_if_valid() {
        assertDoesNotThrow(() -> new OptimalTime(OffsetDateTime.now()));
    }

    @Nested
    class ConvertToOffsetDateTime {

        @Test
        void should_convert() {
            final OffsetDateTime offsetDateTime = OffsetDateTime.now();

            final OptimalTime optimalTime = new OptimalTime(offsetDateTime);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(optimalTime.asOffsetDateTime()).isEqualTo(offsetDateTime);
            softAssertions.assertThat(optimalTime.asOffsetDateTime()).isInstanceOf(OffsetDateTime.class);
            softAssertions.assertAll();
        }
    }

    @Nested
    class IsInFuture {

        @Test
        void should_return_true_if_is_in_future() {
            final OptimalTime nextHour = new OptimalTime(OffsetDateTime.now().plusHours(1));

            assertThat(nextHour.isInFuture()).isTrue();
        }

        @Test
        void should_return_true_if_is_in_now() {
            final OptimalTime now = new OptimalTime(OffsetDateTime.now());

            assertThat(now.isInFuture()).isTrue();
        }

        @Test
        void should_return_false_if_is_in_past() {
            final OptimalTime lastHour = new OptimalTime(OffsetDateTime.now().minusHours(1));

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