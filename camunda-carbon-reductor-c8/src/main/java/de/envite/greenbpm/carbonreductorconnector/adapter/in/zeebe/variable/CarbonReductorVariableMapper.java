package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.springframework.stereotype.Component;

@Component
public class CarbonReductorVariableMapper {

    public CarbonReductorConfiguration mapToDomain(CarbonReductorInputVariable inputVariables) {
        final ExceptionHandlingEnum exceptionHandling = inputVariables.getErrorHandling() != null && !inputVariables.getErrorHandling().isEmpty() ?
                ExceptionHandlingEnum.valueOf(inputVariables.getErrorHandling()) : null;
        return new CarbonReductorConfiguration(
                new Location(inputVariables.getLocation()),
                new Milestone(inputVariables.getMilestone()),
                new Timeshift(inputVariables.getRemainingProcessDuration()),
                mapIfNotNull(inputVariables.getMaximumProcessDuration()),
                mapIfNotNull(inputVariables.getTimeshiftWindow()),
                exceptionHandling,
                inputVariables.isMeasurementOnly());
    }

    private Timeshift mapIfNotNull(String input) {
        if (input == null) {
            return null;
        }
        return new Timeshift(input);
    }

    public CarbonReductorOutputVariable mapFromDomain(CarbonReduction output) {
        CarbonReductorOutputVariable outputVariable = new CarbonReductorOutputVariable();
        outputVariable.setExecutionDelayed(output.getDelay().isExecutionDelayed());
        outputVariable.setDelayedBy(output.getDelay().getDelayedBy());
        outputVariable.setOptimalForecastedCarbon(output.getOptimalForecastedCarbon().getValue());
        outputVariable.setCarbonWithoutOptimization(output.getCarbonWithoutOptimization().getValue());
        outputVariable.setSavedCarbonPercentage(output.getSavedCarbonPercentage().getValue());
        outputVariable.setCarbonReduction(output.calculateReduction().getValue());
        return outputVariable;
    }
}
