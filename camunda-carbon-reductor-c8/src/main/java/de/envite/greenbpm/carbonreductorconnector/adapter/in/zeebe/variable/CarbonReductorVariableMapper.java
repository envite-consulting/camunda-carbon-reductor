package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorMode;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import org.springframework.stereotype.Component;

@Component
public class CarbonReductorVariableMapper {

    public CarbonReductorConfiguration mapToDomain(CarbonReductorInputVariable inputVariables) {
        return new CarbonReductorConfiguration(
                new Location(inputVariables.getLocation()),
                new CarbonReductorMode(inputVariables.getCarbonReductorMode()),
                new Milestone(inputVariables.getMilestone()),
                new Timeshift(inputVariables.getRemainingProcessDuration()),
                mapIfNotNull(inputVariables.getMaximumProcessDuration()),
                mapIfNotNull(inputVariables.getTimeshiftWindow())
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
        outputVariable.setActualCarbon(output.getActualCarbon().getValue());
        outputVariable.setOriginalCarbon(output.getOriginalCarbon().getValue());
        outputVariable.setSavedCarbon(output.getSavedCarbon().getValue());
        outputVariable.setCarbonReduction(output.calculateReduction().getValue());
        return outputVariable;
    }
}
