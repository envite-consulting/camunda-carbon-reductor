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
        validateNotNull(earliestForecastedValue, "Rating");
        validateNotNull(optimalValue, "ForecastedValue");
        evaluateValidations();
    }

    public boolean isCleanerEnergyInFuture() {
        return earliestForecastedValue.getValue() > optimalValue.getValue();
    }

    public double calculateSavedCarbonDelta() {
        return earliestForecastedValue.getValue() - optimalValue.getValue();

    }
    public double calculateSavedCarbonPercentage() {
        double difference = earliestForecastedValue.getValue() - optimalValue.getValue();
        return (difference / earliestForecastedValue.getValue()) * 100;
    }
}
