package de.envite.greenbpm.carbonreductorconnector.usecase.out;

import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.location.Location;

public interface CarbonEmissionQuery {

    EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException;
}
