package de.envite.greenbpm.carbonreductor.core.usecase.out;

import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import org.springframework.cache.annotation.Cacheable;

import static de.envite.greenbpm.carbonreductor.core.technology.Constants.EMISSION_TIMEFRAME_NAME;

public interface CarbonEmissionQuery {

    @Cacheable(EMISSION_TIMEFRAME_NAME)
    EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime) throws CarbonEmissionQueryException;
}
