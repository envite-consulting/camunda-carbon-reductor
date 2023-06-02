package de.envite.greenbpm.carbonreductor.core.usecase.in;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;

public interface DelayCalculator {

    /**
     * Calculates the delay depending on the input parameters
     * @param input CarbonReductorConfiguration specifying the configuration for the possible postponement
     * @return CarbonReduction reporting the possible reduction and savings
     * @throws CarbonReductorException depending on the {@link de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum} option an exception is either thrown or caught and logged
     */
    CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException;
}
