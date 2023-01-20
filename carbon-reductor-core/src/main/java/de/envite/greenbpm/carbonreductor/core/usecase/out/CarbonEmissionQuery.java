package de.envite.greenbpm.carbonreductor.core.usecase.out;

import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;

public interface CarbonEmissionQuery {

    EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException;
}
