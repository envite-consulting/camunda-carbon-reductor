package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import io.github.domainprimitives.object.Aggregate;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class EmissionTimeframe extends Aggregate {

    /**
     * Optimal execution time with lowest CO2eq for the specific timeframe.
     * Value is represented by {@link EmissionTimeframe#optimalValue}.
     */
    private final OptimalTime optimalTime;

    /**
     * Earliest forecasted value (CO2eq). Could be null because some data provider only serve the {@link EmissionTimeframe#optimalValue}.
     * Usefully for comparing the savings.
     */
    private final EarliestForecastedValue earliestForecastedValue;

    /**
     * Optimal forecasted value for the execution with lowest CO2eq for the specific timeframe.
     * Execution time when this value could be reached is represented by {@link EmissionTimeframe#optimalTime}.
     */
    private final ForecastedValue optimalValue;

    public EmissionTimeframe(OptimalTime optimalTime, EarliestForecastedValue earliestForecastedValue, ForecastedValue optimalValue) {
        this.optimalTime = optimalTime;
        this.earliestForecastedValue = earliestForecastedValue;
        this.optimalValue = optimalValue;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(optimalTime, "Optimal Time");
        validateNotNull(optimalValue, "ForecastedValue");
        evaluateValidations();
    }

    public boolean isCleanerEnergyInFuture() {
        if (earliestForecastedValue == null) {
            return optimalTime.isInFuture();
        }
        final boolean emissionReduction = earliestForecastedValue.getValue() > optimalValue.getValue();
        return emissionReduction && optimalTime.isInFuture();
    }

    public double calculateSavedCarbonDelta() {
        if (earliestForecastedValue == null) {
            return 0.0;
        }
        return earliestForecastedValue.getValue() - optimalValue.getValue();
    }

    public double calculateSavedCarbonPercentage() {
        if (earliestForecastedValue == null) {
            return 100;
        }
        double difference = earliestForecastedValue.getValue() - optimalValue.getValue();
        return (difference / earliestForecastedValue.getValue()) * 100;
    }
}
