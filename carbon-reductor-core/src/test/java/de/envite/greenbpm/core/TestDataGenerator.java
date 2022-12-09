package de.envite.greenbpm.core;


import de.envite.greenbpm.core.domain.model.CarbonReduction;
import de.envite.greenbpm.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.core.domain.model.input.Milestone;
import de.envite.greenbpm.core.domain.model.input.Timeshift;
import de.envite.greenbpm.core.domain.model.input.carbonreductormode.CarbonReductorModes;
import de.envite.greenbpm.core.domain.model.output.Carbon;
import de.envite.greenbpm.core.domain.model.output.Delay;

import static de.envite.greenbpm.core.domain.model.input.location.Locations.NORWAY_EAST;

public class TestDataGenerator {

    public static CarbonReductorConfiguration createTimeshiftWindowCarbonReductorInput(String timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new Milestone(timestamp),
                new Timeshift("PT5M"),
                null,
                new Timeshift("PT10M")
        );
    }

    public static CarbonReductorConfiguration createTimeshiftWindowIsExceededByRemainingDuration(String timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new Milestone(timestamp),
                new Timeshift("PT1H"),
                null,
                new Timeshift("PT30M")
        );
    }

    public static CarbonReductorConfiguration createSLABasedCarbonReductorInput(String timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                new Milestone(timestamp),
                new Timeshift("PT5H"),
                new Timeshift("PT10H"),
                null
        );
    }

    public static CarbonReduction createCarbonReductorOutput() {
        return new CarbonReduction(
                new Delay(true, 3),
                new Carbon(1.0),
                new Carbon(2.0),
                new Carbon(3.0)
        );
    }
}
