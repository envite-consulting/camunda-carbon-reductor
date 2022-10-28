package de.envite.greenbpm.carbonreductorconnector.usecase.in;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductorconnector.domain.service.CarbonReductorException;

public interface DelayCalculator {
    CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException;
}
