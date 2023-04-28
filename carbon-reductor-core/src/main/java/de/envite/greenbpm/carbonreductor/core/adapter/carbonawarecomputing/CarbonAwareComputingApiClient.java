package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.api.ForecastApi;
import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsForecast;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Client for the free Carbon Aware Computing API
 * https://forecast.carbon-aware-computing.com/swagger/UI
 */
@Slf4j
@RequiredArgsConstructor
public class CarbonAwareComputingApiClient implements CarbonEmissionQuery {

    private final ForecastApi forecastApi;
    private final LocationMapper locationMapper;
    private final CarbonAwareComputingMapper carbonAwareComputingMapper;

    @Override
    public EmissionTimeframe getEmissionTimeframe(Location location, Timeshift timeshift, Timeshift executiontime)
            throws CarbonEmissionQueryException {
        final int windowSizeMinutes = 5;
        EmissionsForecast emissionsForecast = null;
        try {
            final String mappedLocation = locationMapper.mapLocation(location);
            emissionsForecast = forecastApi.getBestExecutionTime(List.of(mappedLocation), null, timeshift.timeshiftFromNow(), windowSizeMinutes);
        } catch (Exception e) {
            log.error("Error when calling the API for the optimal forecast", e);
            throw new CarbonEmissionQueryException(e);
        }

        return ofNullable(emissionsForecast)
                .map(EmissionsForecast::getOptimalDataPoints)
                .filter(d -> !d.isEmpty())
                .map(d -> d.get(0))
                .map(carbonAwareComputingMapper::mapToDoamin)
                .orElseThrow(() -> new CarbonEmissionQueryException("API provided no data"));
    }
}
