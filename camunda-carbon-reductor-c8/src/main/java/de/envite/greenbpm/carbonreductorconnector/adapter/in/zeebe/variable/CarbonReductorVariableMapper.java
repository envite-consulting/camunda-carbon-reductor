package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Threshold;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;

@Component
public class CarbonReductorVariableMapper {

    // Sample Date: 2023-09-08T12:58:20.766Z[GMT]
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX'['z']'";

    private static OffsetDateTime parseDateString(String value) {
        try {
            return OffsetDateTime.parse(value, ofPattern(DATE_TIME_PATTERN));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    format("Milestone: Unknown date time format (Expected %s)",
                            DATE_TIME_PATTERN.replace("'", "")
                    )
            );
        }
    }

    public CarbonReductorConfiguration mapToDomain(CarbonReductorInputVariable inputVariables) {
        final ExceptionHandlingEnum exceptionHandling = inputVariables.getErrorHandling() != null && !inputVariables.getErrorHandling().isEmpty() ?
                ExceptionHandlingEnum.valueOf(inputVariables.getErrorHandling()) : null;
        return new CarbonReductorConfiguration(
                new Location(inputVariables.getLocation()),
                new Milestone(parseDateString(inputVariables.getMilestone())),
                new Timeshift(inputVariables.getRemainingProcessDuration()),
                mapIfNotNull(inputVariables.getMaximumProcessDuration()),
                mapIfNotNull(inputVariables.getTimeshiftWindow()),
                exceptionHandling,
                inputVariables.isMeasurementOnly(),
                new Threshold(inputVariables.isThresholdEnabled(), inputVariables.getThresholdValue())
        );
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
