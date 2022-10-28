package de.envite.greenbpm.carbonreductorconnector.usecase.out;

import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.location.Location;

public interface CarbonEmissionQuery {

    EmissionTimeframe getCurrentEmission(Location location, Duration duration, Duration executiontime) throws CarbonEmissionQueryException;
}
