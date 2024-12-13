package de.envite.greenbpm.carbonreductor.core.domain.model;


import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EmissionTimeframeTest {

    private final OptimalTime optimalTime = new OptimalTime(OffsetDateTime.now());
    private final EarliestForecastedValue earliestForecastedValue = new EarliestForecastedValue(200.0);
    private final ForecastedValue forecastedValue = new ForecastedValue(50.0);

    @Nested
    class CrossValidation {

        @Test
        void should_create_valid_object() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, earliestForecastedValue, forecastedValue);

            assertThat(emissionTimeframe.getOptimalTime()).isEqualTo(optimalTime);
            assertThat(emissionTimeframe.getEarliestForecastedValue()).isEqualTo(earliestForecastedValue);
            assertThat(emissionTimeframe.getOptimalValue()).isEqualTo(forecastedValue);
        }

        @Test
        void should_throw_if_optimal_time_is_null() {
            assertThatThrownBy(() -> new EmissionTimeframe(null, earliestForecastedValue, forecastedValue))
                    .isInstanceOf(InvariantException .class);
        }

        @Test
        void should_throw_if_forecast_is_null() {
            assertThatThrownBy(() -> new EmissionTimeframe(optimalTime, earliestForecastedValue, null))
                    .isInstanceOf(InvariantException .class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CleanEnergyInFutureCalculation {

        @ParameterizedTest
        @MethodSource("emissionTimeframeParams")
        void should_calculate(
                OptimalTime optimalTime,
                EarliestForecastedValue earliestForecastedValue,
                ForecastedValue optimalValue,
                boolean expectation
        ) {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, earliestForecastedValue, optimalValue);

            assertThat(emissionTimeframe.isCleanerEnergyInFuture()).isEqualTo(expectation);
        }

        Stream<Arguments> emissionTimeframeParams() {

            return Stream.of(
                    Arguments.of(optimalTime, earliestForecastedValue, forecastedValue, true),
                    Arguments.of(optimalTime, null, forecastedValue, true),
                    Arguments.of(optimalTime, earliestForecastedValue, new ForecastedValue(500.0), false),
                    Arguments.of(new OptimalTime(OffsetDateTime.now().minusHours(1)), earliestForecastedValue, forecastedValue, false),
                    Arguments.of(new OptimalTime(OffsetDateTime.now().minusHours(1)), earliestForecastedValue, new ForecastedValue(500.0), false)
            );
        }
    }

    @Nested
    class CalculateSavedCarbonDelta {
        @Test
        void should_calculate_saved_carbon_delta() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, earliestForecastedValue, forecastedValue);

            assertThat(emissionTimeframe.calculateSavedCarbonDelta()).isEqualTo(earliestForecastedValue.getValue() - forecastedValue.getValue());
        }

        @Test
        void should_return_zero_on_calculate_saved_carbon_delta_if_earliest_is_null() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, null, forecastedValue);

            assertThat(emissionTimeframe.calculateSavedCarbonDelta()).isZero();
        }
    }

    @Nested
    class SavedCarbonCalculationInPercentage {

        @Test
        void should_calculate_saved_carbon_in_percentage() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, earliestForecastedValue, forecastedValue);

            assertThat(emissionTimeframe.calculateSavedCarbonPercentage()).isEqualTo(75.0);
        }

        @Test
        void should_return_optimal_on_calculate_saved_carbon_in_percentage_if_earliest_is_null() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, null, forecastedValue);

            assertThat(emissionTimeframe.calculateSavedCarbonPercentage()).isEqualTo(100);
        }
    }
}