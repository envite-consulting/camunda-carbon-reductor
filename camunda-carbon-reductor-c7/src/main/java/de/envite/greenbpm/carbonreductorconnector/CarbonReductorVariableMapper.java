package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorMode;
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
        return new CarbonReductorConfiguration(
                new Location((String) allVariables.get("location")),
                new CarbonReductorMode((String) allVariables.get("carbonReductorMode")),
                new Milestone(getDateTime(allVariables)),
                mapIfNotNull((String) allVariables.get("remainingProcessDuration")),
                mapIfNotNull((String) allVariables.get("maximumProcessDuration")),
                null // Will become relevant in the future
        );
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
        variables.put("originalCarbon", output.getOriginalCarbon().getValue());
        variables.put("actualCarbon", output.getActualCarbon().getValue());
        variables.put("savedCarbon", output.getSavedCarbon().getValue());
        variables.put("reducedCarbon", output.calculateReduction().getValue());
        variables.put("delayedBy", output.getDelay().getDelayedBy());
        // Override milestone variable because joda time is not a primitive object ..
        variables.put("milestone", getDateTime(allVariables));
        return variables;
    }
}
