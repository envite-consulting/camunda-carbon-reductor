package de.envite.greenbpm.carbonreductor.core.usecase.in;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;

public interface DelayCalculator {
    CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException;
}
