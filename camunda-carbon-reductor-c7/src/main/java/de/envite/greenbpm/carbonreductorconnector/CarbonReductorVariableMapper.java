package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


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
                null, // Will become relevant in the future
                exceptionHandling,
                Boolean.parseBoolean((String) allVariables.get("measurementOnly")), threshold);
    }

    private OffsetDateTime getMilestone(Map<String, Object> allVariables) {
        // "2023-09-08T14:13:40.764+02:00"
        return OffsetDateTime.parse((String) allVariables.get("milestone"));
    }

    private Timeshift mapIfNotNull(String input) {
        if (input == null) {
            return null;
        }
        return new Timeshift(input);
    }

    public Map<String, Object> mapFromDomain(CarbonReduction output, Map<String, Object> allVariables) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("executionDelayed", output.getDelay().isExecutionDelayed());
        variables.put("carbonWithoutOptimization", output.getCarbonWithoutOptimization().getValue());
        variables.put("optimalForecastedCarbon", output.getOptimalForecastedCarbon().getValue());
        variables.put("savedCarbonPercentage", output.getSavedCarbonPercentage().getValue());
        variables.put("reducedCarbon", output.calculateReduction().getValue());
        variables.put("delayedBy", output.getDelay().getDelayedBy());
        // Override milestone variable because joda time is not a primitive object ..
        variables.put("milestone", getMilestone(allVariables).format(DateTimeFormatter.ISO_DATE_TIME));
        return variables;
    }
}
