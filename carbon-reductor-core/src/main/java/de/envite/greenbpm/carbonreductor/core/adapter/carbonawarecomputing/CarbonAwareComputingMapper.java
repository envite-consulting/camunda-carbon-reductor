package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;

public class CarbonAwareComputingMapper {

    public EmissionTimeframe mapToDomain(EmissionsData emissionsData) {
        return new EmissionTimeframe(
                new OptimalTime(emissionsData.getTimestamp()),
                null,
                new ForecastedValue(emissionsData.getValue())
        );
    }
}
