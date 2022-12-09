package de.envite.greenbpm.core.usecase.in;

import de.envite.greenbpm.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.core.domain.model.CarbonReduction;
import de.envite.greenbpm.core.domain.service.CarbonReductorException;

public interface DelayCalculator {
    CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException;
}
