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

    private final OptimalTime optimalTime;
    private final EarliestForecastedValue earliestForecastedValue;
    private final ForecastedValue forecastedValue;

    public EmissionTimeframe(OptimalTime optimalTime, EarliestForecastedValue earliestForecastedValue, ForecastedValue forecastedValue) {
        this.optimalTime = optimalTime;
        this.earliestForecastedValue = earliestForecastedValue;
        this.forecastedValue = forecastedValue;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(optimalTime, "Optimal Time");
        validateNotNull(earliestForecastedValue, "Rating");
        validateNotNull(forecastedValue, "ForecastedValue");
        evaluateValidations();
    }

    public boolean isCleanerEnergyInFuture() {
        return earliestForecastedValue.getValue() > forecastedValue.getValue();
    }

    public double calculateSavedCarbonDelta() {
        return earliestForecastedValue.getValue() - forecastedValue.getValue();

    }
    public double calculateSavedCarbonPercentage() {
        double difference = earliestForecastedValue.getValue() - forecastedValue.getValue();
        return (difference / earliestForecastedValue.getValue()) * 100;
    }
}
