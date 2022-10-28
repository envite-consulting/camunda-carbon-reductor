package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorInputVariable;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.carbonreductormode.CarbonReductorModes;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.input.location.Locations.NORWAY_EAST;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.mockito.Mockito.when;

public class TestDataGenerator {

    public static CarbonReductorConfiguration createTimeshiftWindowCarbonReductorInput() {
        return createTimeshiftWindowCarbonReductorInput("2022-10-20T11:35:45.826Z[Etc/UTC]");
    }

    public static CarbonReductorConfiguration createTimeshiftWindowCarbonReductorInput(String timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new Milestone(timestamp),
                new Duration("PT5M"),
                null,
                new Duration("PT10M")
        );
    }

    public static CarbonReductorConfiguration createTimeshiftWindowIsExceededByRemainingDuration(String timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new Milestone(timestamp),
                new Duration("PT1H"),
                null,
                new Duration("PT30M")
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
                new Duration("PT5H"),
                new Duration("PT10H"),
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
}
