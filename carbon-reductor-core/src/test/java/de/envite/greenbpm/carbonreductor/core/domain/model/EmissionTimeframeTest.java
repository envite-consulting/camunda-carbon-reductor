package de.envite.greenbpm.carbonreductor.core.domain.model;


import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EmissionTimeframeTest {

    private final OptimalTime optimalTime = new OptimalTime(OffsetDateTime.now());
    private final Rating rating = new Rating(200.0);
    private final ForecastedValue forecastedValue = new ForecastedValue(50.0);

    @Nested
    class CrossValidation {

        @Test
        void should_create_valid_object() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, rating, forecastedValue);

            assertThat(emissionTimeframe.getOptimalTime()).isEqualTo(optimalTime);
            assertThat(emissionTimeframe.getRating()).isEqualTo(rating);
            assertThat(emissionTimeframe.getForecastedValue()).isEqualTo(forecastedValue);
        }

        @Test
        void should_throw_if_optimal_time_is_null() {
            assertThatThrownBy(() -> new EmissionTimeframe(null, rating, forecastedValue))
                    .isInstanceOf(InvariantException .class);
        }

        @Test
        void should_throw_if_rating_is_null() {
            assertThatThrownBy(() -> new EmissionTimeframe(optimalTime, null, forecastedValue))
                    .isInstanceOf(InvariantException .class);
        }

        @Test
        void should_throw_if_forecast_is_null() {
            assertThatThrownBy(() -> new EmissionTimeframe(optimalTime, rating, null))
                    .isInstanceOf(InvariantException .class);
        }
    }

    @Nested
    class CleanEnergyInFutureCalculation {

        @Test
        void should_return_false_if_rating_is_smaller_than_forecast() {
            ForecastedValue forecastedValue = new ForecastedValue(500.0);
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, rating, forecastedValue);

            assertThat(emissionTimeframe.isCleanerEnergyInFuture()).isFalse();
        }

        @Test
        void should_return_true_if_rating_is_greater_than_forecast() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, rating, forecastedValue);

            assertThat(emissionTimeframe.isCleanerEnergyInFuture()).isTrue();
        }
    }

    @Nested
    class SavedCarbonCalculationInPercentage {

        @Test
        void should_calculate_saved_carbon_in_percentage() {
            EmissionTimeframe emissionTimeframe = new EmissionTimeframe(optimalTime, rating, forecastedValue);

            assertThat(emissionTimeframe.calculateSavedCarbonPercentage()).isEqualTo(75.0);
        }
    }
}