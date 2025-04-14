package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import io.github.domainprimitives.validation.InvariantException;
import io.skippy.junit5.PredictWithSkippy;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PredictWithSkippy
class CarbonReductionTest {

    @Nested
    class CrossValidation {
        private final Carbon carbon = new Carbon(1.0);
        private final Delay delay = new Delay(true, 1);
        private final Percentage percentage = new Percentage(0.8);

        @Test
        void should_throw_if_no_delay() {
            assertThatThrownBy(() -> new CarbonReduction(null, carbon, carbon, percentage))
                    .isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_if_no_actual_carbon() {
            assertThatThrownBy(() -> new CarbonReduction(delay, carbon, null, percentage))
                    .isInstanceOf(InvariantException.class);
        }
    }

    @Nested
    class ReductionCalculation {
        @Test
        void should_calculate_reduction() {
            CarbonReduction carbonReduction = new CarbonReduction(
                    new Delay(true, 3),
                    new Carbon(1.0),
                    new Carbon(2.0),
                    new Percentage(0.3)
            );
            Double expectedReduction = carbonReduction.getCarbonWithoutOptimization().getValue() -
                    carbonReduction.getOptimalForecastedCarbon().getValue();

            Carbon reduction = carbonReduction.calculateReduction();

            assertThat(reduction.getValue()).isEqualTo(expectedReduction);
        }

        @Test
        void should_return_null_on_calculate_reduction_if_no_original() {
            CarbonReduction carbonReduction = new CarbonReduction(
                    new Delay(true, 3),
                    null,
                    new Carbon(2.0),
                    new Percentage(0.3)
            );

            Carbon reduction = carbonReduction.calculateReduction();

            assertThat(reduction).isNull();
        }
    }

}