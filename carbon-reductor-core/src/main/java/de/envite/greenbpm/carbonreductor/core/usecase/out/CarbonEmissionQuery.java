package de.envite.greenbpm.carbonreductor.core.usecase.out;

import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;

public interface CarbonEmissionQuery {

    EmissionTimeframe getEmissionTimeframe(Location location, ProcessDuration processDuration, ProcessDuration executiontime) throws CarbonEmissionQueryException;
}
