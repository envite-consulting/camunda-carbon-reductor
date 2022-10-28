package de.envite.greenbpm.carbonreductorconnector.domain.model;

import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Delay;
import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static de.envite.greenbpm.carbonreductorconnector.TestDataGenerator.createCarbonReductorOutput;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonReductionTest {

    @Nested
    class CrossValidation {
        private final Carbon carbon = new Carbon(1.0);
        private final Delay delay = new Delay(true, 1);

        @Test
        void should_throw_if_no_delay() {
            assertThatThrownBy(() -> new CarbonReduction(null, carbon, carbon, carbon))
                    .isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_if_no_original_carbon() {
            assertThatThrownBy(() -> new CarbonReduction(delay, null, carbon, carbon))
                    .isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_if_no_actual_carbon() {
            assertThatThrownBy(() -> new CarbonReduction(delay, carbon, null, carbon))
                    .isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_if_no_saved_carbon() {
            assertThatThrownBy(() -> new CarbonReduction(delay, carbon, carbon, null))
                    .isInstanceOf(InvariantException.class);
        }
    }

    @Test
    void should_calculate_reduction() {
        CarbonReduction carbonReduction = createCarbonReductorOutput();
        Double expectedReduction = carbonReduction.getOriginalCarbon().getValue() -
                carbonReduction.getActualCarbon().getValue();

        Carbon reduction = carbonReduction.calculateReduction();

        assertThat(reduction.getValue()).isEqualTo(expectedReduction);
    }
}