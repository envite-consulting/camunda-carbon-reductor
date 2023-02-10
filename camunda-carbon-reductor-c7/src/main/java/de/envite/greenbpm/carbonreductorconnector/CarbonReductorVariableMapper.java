package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorMode;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
class CarbonReductorVariableMapper {
    public CarbonReductorConfiguration mapToDomain(Map<String, Object> allVariables) {
        return new CarbonReductorConfiguration(
                new Location((String) allVariables.get("location")),
                new CarbonReductorMode((String) allVariables.get("carbonReductorMode")),
                new Milestone((String) allVariables.get("milestone")),
                null, // Will become relevant in the future
                mapIfNotNull((String) allVariables.get("maximumProcessDuration")),
                null // Will become relevant in the future
        );
    }

    private Timeshift mapIfNotNull(String input) {
        if (input == null) {
            return null;
        }
        return new Timeshift(input);
    }

    public Map<String, Object> mapFromDomain(CarbonReduction output) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("executionDelayed", output.getDelay().isExecutionDelayed());
        variables.put("originalCarbon", output.getOriginalCarbon().getValue());
        variables.put("actualCarbon", output.getActualCarbon().getValue());
        variables.put("savedCarbon", output.getSavedCarbon().getValue());
        variables.put("carbonReduction", output.calculateReduction().getValue());
        variables.put("delayedBy", output.getDelay().getDelayedBy());
        return variables;
    }
}
