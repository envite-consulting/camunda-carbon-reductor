package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

@Getter
public class EmissionTimeframe extends Aggregate {

    private final OptimalTime optimalTime;
    private final Rating rating;
    private final ForecastedValue forecastedValue;

    public EmissionTimeframe(OptimalTime optimalTime, Rating rating, ForecastedValue forecastedValue) {
        this.optimalTime = optimalTime;
        this.rating = rating;
        this.forecastedValue = forecastedValue;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(optimalTime, "Optimal Time");
        validateNotNull(rating, "Rating");
        validateNotNull(forecastedValue, "ForecastedValue");
        evaluateValidations();
    }

    public boolean isCleanerEnergyInFuture() {
        return rating.getValue() > forecastedValue.getValue();
    }

    public double calculateSavedCarbonPercentage() {
        double difference = rating.getValue() - forecastedValue.getValue();
        return (difference / rating.getValue()) * 100;
    }
}
