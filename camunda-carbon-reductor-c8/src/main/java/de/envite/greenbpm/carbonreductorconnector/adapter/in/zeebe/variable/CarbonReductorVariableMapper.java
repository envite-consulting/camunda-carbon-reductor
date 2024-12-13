package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Threshold;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import io.github.domainprimitives.type.ValueObject;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

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
                new ProcessDuration(inputVariables.getRemainingProcessDuration()),
                mapIfNotNull(inputVariables.getMaximumProcessDuration()),
                exceptionHandling,
                inputVariables.isMeasurementOnly(),
                new Threshold(inputVariables.isThresholdEnabled(), inputVariables.getThresholdValue())
        );
    }

    private ProcessDuration mapIfNotNull(String input) {
        if (input == null) {
            return null;
        }
        return new ProcessDuration(input);
    }

    public CarbonReductorOutputVariable mapFromDomain(CarbonReduction output) {
        final Double carbonWithoutOptimization = Optional.ofNullable(output.getCarbonWithoutOptimization())
                .map(ValueObject::getValue)
                .orElse(null);
        CarbonReductorOutputVariable outputVariable = new CarbonReductorOutputVariable();
        outputVariable.setExecutionDelayed(output.getDelay().isExecutionDelayed());
        outputVariable.setDelayedBy(output.getDelay().getDelayedBy());
        outputVariable.setOptimalForecastedCarbon(output.getOptimalForecastedCarbon().getValue());
        outputVariable.setCarbonWithoutOptimization(carbonWithoutOptimization);
        outputVariable.setSavedCarbonPercentage(output.getSavedCarbonPercentage().getValue());
        outputVariable.setCarbonReduction(output.calculateReduction().getValue());
        return outputVariable;
    }
}
