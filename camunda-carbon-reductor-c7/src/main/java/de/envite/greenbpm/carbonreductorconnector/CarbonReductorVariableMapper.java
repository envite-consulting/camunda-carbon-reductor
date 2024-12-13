package de.envite.greenbpm.carbonreductorconnector;

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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
class CarbonReductorVariableMapper {
    public CarbonReductorConfiguration mapToDomain(Map<String, Object> allVariables) {
        final ExceptionHandlingEnum exceptionHandling = allVariables.containsKey("errorHandling") ?
                ExceptionHandlingEnum.valueOf((String) allVariables.get("errorHandling")) : null;
        return new CarbonReductorConfiguration(
                new Location((String) allVariables.get("location")),
                new Milestone(getMilestone(allVariables)),
                mapIfNotNull((String) allVariables.get("remainingProcessDuration")),
                mapIfNotNull((String) allVariables.get("maximumProcessDuration")),
                exceptionHandling,
                Boolean.parseBoolean((String) allVariables.get("measurementOnly")),
                mapIfEnabled(
                        Boolean.parseBoolean((String) allVariables.get("thresholdEnabled")),
                        (String) allVariables.get("thresholdValue")
                )
        );
    }

    private OffsetDateTime getMilestone(Map<String, Object> allVariables) {
        // "2023-09-08T14:13:40.764+02:00"
        return OffsetDateTime.parse((String) allVariables.get("milestone"));
    }

    private ProcessDuration mapIfNotNull(String input) {
        if (input == null) {
            return null;
        }
        return new ProcessDuration(input);
    }

    private Threshold mapIfEnabled(boolean enabled, String thresholdValue) {
        if (enabled) {
            return new Threshold(enabled, Float.valueOf(thresholdValue));
        }
        return new Threshold(enabled, 0.0f);
    }

    public Map<String, Object> mapFromDomain(CarbonReduction output, Map<String, Object> allVariables) {
        final Double carbonWithoutOptimization = Optional.ofNullable(output.getCarbonWithoutOptimization())
                .map(ValueObject::getValue)
                .orElse(null);
        Map<String, Object> variables = new HashMap<>();
        variables.put("executionDelayed", output.getDelay().isExecutionDelayed());
        variables.put("carbonWithoutOptimization", carbonWithoutOptimization);
        variables.put("optimalForecastedCarbon", output.getOptimalForecastedCarbon().getValue());
        variables.put("savedCarbonPercentage", output.getSavedCarbonPercentage().getValue());
        variables.put("reducedCarbon", output.calculateReduction().getValue());
        variables.put("delayedBy", output.getDelay().getDelayedBy());
        // Override milestone variable because joda time is not a primitive object ..
        variables.put("milestone", getMilestone(allVariables).format(DateTimeFormatter.ISO_DATE_TIME));
        return variables;
    }
}
