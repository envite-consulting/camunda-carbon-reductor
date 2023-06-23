package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;

public class CarbonAwareComputingMapper {

    public EmissionTimeframe mapToDoamin(EmissionsData emissionsData) {
        return new EmissionTimeframe(
                new OptimalTime(emissionsData.getTimestamp()),
                new EarliestForecastedValue(0.0),
                new ForecastedValue(emissionsData.getValue())
        );
    }
}
