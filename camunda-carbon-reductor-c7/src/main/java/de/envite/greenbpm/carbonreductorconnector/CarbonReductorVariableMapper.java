package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone.YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC;

@Component
class CarbonReductorVariableMapper {
    public CarbonReductorConfiguration mapToDomain(Map<String, Object> allVariables) {
        final ExceptionHandlingEnum exceptionHandling = allVariables.containsKey("errorHandling") ?
                ExceptionHandlingEnum.valueOf((String) allVariables.get("errorHandling")) : null;
        return new CarbonReductorConfiguration(
                new Location((String) allVariables.get("location")),
                new Milestone(getDateTime(allVariables)),
                mapIfNotNull((String) allVariables.get("remainingProcessDuration")),
                mapIfNotNull((String) allVariables.get("maximumProcessDuration")),
                null, // Will become relevant in the future
                exceptionHandling,
                Boolean.parseBoolean((String) allVariables.get("measurementOnly")));
    }

    private String getDateTime(Map<String, Object> allVariables) {
        DateTime dt = (DateTime) allVariables.get("milestone");
        long millis = dt.getMillis();
        return Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC));
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
        variables.put("milestone", getDateTime(allVariables));
        return variables;
    }
}
