package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.Duration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.ExecutionTimestamp;
import de.envite.greenbpm.carbonreductorconnector.domain.model.carbonreductormode.CarbonReductorModes;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.location.Locations.NORWAY_EAST;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class TestDataGenerator {

    public static CarbonReductorInput createTimeshiftWindowCarbonReductorInput() {
        return createTimeshiftWindowCarbonReductorInput("2022-10-20T11:35:45.826Z[Etc/UTC]");
    }

    public static CarbonReductorInput createTimeshiftWindowCarbonReductorInput(String timestamp) {
        return new CarbonReductorInput(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new ExecutionTimestamp(timestamp),
                new Duration("PT5M"),
                null,
                new Duration("PT10M")
        );
    }

    public static CarbonReductorInput createTimeshiftWindowIsExceededByRemainingDuration(String timestamp) {
        return new CarbonReductorInput(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.asCarbonReductorMode(),
                new ExecutionTimestamp(timestamp),
                new Duration("PT1H"),
                null,
                new Duration("PT30M")
        );
    }

    public static CarbonReductorInput createSLABasedCarbonReductorInput() {
        return createSLABasedCarbonReductorInput("2022-10-20T11:35:45.826Z[Etc/UTC]");
    }

    public static CarbonReductorInput createSLABasedCarbonReductorInput(String timestamp) {
        return new CarbonReductorInput(
                NORWAY_EAST.asLocation(),
                CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                new ExecutionTimestamp(timestamp),
                new Duration("PT5H"),
                new Duration("PT10H"),
                null
        );
    }
}
