package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorInputVariable;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.carbonreductormode.CarbonReductorModes;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Delay;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.input.location.Locations.NORWAY_EAST;

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

    public static CarbonReductorConfiguration createSLABasedCarbonReductorInput() {
        return createSLABasedCarbonReductorInput("2022-10-20T11:35:45.826Z[Etc/UTC]");
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

    public static CarbonReductorInputVariable createInputVariables() {
        CarbonReductorInputVariable inputVariable = new CarbonReductorInputVariable();
        inputVariable.setLocation("norwayeast");
        inputVariable.setCarbonReductorMode("timeshiftWindowOnly");
        inputVariable.setMilestone("2022-10-20T11:35:45.826Z[Etc/UTC]");
        inputVariable.setRemainingProcessDuration("PT10M");
        inputVariable.setTimeshiftWindow("PT6H");
        return inputVariable;
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
