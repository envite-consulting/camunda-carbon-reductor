package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.api.ForecastApi;
import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsForecast;
import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
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
    public EmissionTimeframe getEmissionTimeframe(Location location, ProcessDuration processDuration, ProcessDuration executiontime)
            throws CarbonEmissionQueryException {
        List<EmissionsForecast> emissionsForecast = null;
        final String mappedLocation = locationMapper.mapLocation(location);
        if (mappedLocation == null) {
            throw new CarbonEmissionQueryException("The location is not known yet.");
        }

        try {
            emissionsForecast = forecastApi.getBestExecutionTime(mappedLocation, null, processDuration.timeshiftFromNow(), executiontime.inMinutes());
        } catch (Exception e) {
            log.error("Error when calling the API for the optimal forecast", e);
            throw new CarbonEmissionQueryException(e);
        }

        return ofNullable(emissionsForecast)
                .filter(d -> !d.isEmpty())
                .map(d -> d.get(0))
                .map(EmissionsForecast::getOptimalDataPoints)
                .filter(d -> !d.isEmpty())
                .map(d -> d.get(0))
                .map(carbonAwareComputingMapper::mapToDomain)
                .orElseThrow(() -> new CarbonEmissionQueryException("API provided no data"));
    }
}
