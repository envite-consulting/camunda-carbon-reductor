package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ThresholdTest {

    @Test
    void should_create_valid_object() {
        assertDoesNotThrow(() -> new Threshold(true, 1.1f));
    }

    @Test
    void should_throw_if_enable_and_less_than_one() {
        assertThatThrownBy(() -> new Threshold(true, 0.9f))
                .isInstanceOf(InvariantException.class)
                .hasMessageContaining("The Threshold must be greater than 1");
    }

    @Nested
    class GreaterThanForecastedValue {

        @Test
        void should_return_true_if_disabled_to_not_interfere() {
            final Threshold threshold = new Threshold(false, 0f);
            assertThat(threshold.isGreaterThanMinimumThreshold(1.0)).isTrue();
        }

        @Test
        void should_return_true_if_enabled_and_equal() {
            final Threshold threshold = new Threshold(true, 1.0f);
            assertThat(threshold.isGreaterThanMinimumThreshold(1.0)).isTrue();
        }

        @Test
        void should_return_true_if_enabled_and_above() {
            final Threshold threshold = new Threshold(true, 1.0f);
            assertThat(threshold.isGreaterThanMinimumThreshold(1.1)).isTrue();
        }

        @Test
        void should_return_false_if_enabled_and_below() {
            final Threshold threshold = new Threshold(true, 1.1f);
            assertThat(threshold.isGreaterThanMinimumThreshold(0.1)).isFalse();
        }
    }
}
