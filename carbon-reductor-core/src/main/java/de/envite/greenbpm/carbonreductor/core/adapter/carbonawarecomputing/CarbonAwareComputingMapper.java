package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;

import java.time.OffsetDateTime;

public class CarbonAwareComputingMapper {

    public EmissionTimeframe mapToDomain(EmissionsData emissionsData) {
        if (emissionsData.getTimestamp() != null && emissionsData.getTimestamp().isAfter(OffsetDateTime.now())) {
            return new EmissionTimeframe(
                    new OptimalTime(emissionsData.getTimestamp()),
                    new EarliestForecastedValue(0.0),
                    new ForecastedValue(emissionsData.getValue())
            );
        }

        return new EmissionTimeframe(
                new OptimalTime(emissionsData.getTimestamp()),
                new EarliestForecastedValue(emissionsData.getValue()),
                new ForecastedValue(emissionsData.getValue())
        );
    }
}
