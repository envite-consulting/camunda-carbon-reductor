package de.envite.greenbpm.core.usecase.out;

import de.envite.greenbpm.core.adapter.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.core.domain.model.input.Timeshift;
import de.envite.greenbpm.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.core.domain.model.input.location.Location;

public interface CarbonEmissionQuery {

    EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException;
}
