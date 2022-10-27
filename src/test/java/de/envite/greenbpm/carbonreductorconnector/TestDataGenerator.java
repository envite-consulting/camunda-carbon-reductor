package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.Duration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.ExeceutionTimestamp;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.location.Locations.NORWAY_EAST;

public class TestDataGenerator {
    public static CarbonReductorInput createCarbonReductorInput() {
        return createCarbonReductorInput("2022-10-20T11:35:45.826Z[Etc/UTC]");
    }

    public static CarbonReductorInput createCarbonReductorInput(String timestamp) {
        return new CarbonReductorInput(
                new Duration("PT5M"),
                NORWAY_EAST.asLocation(),
                new ExeceutionTimestamp(timestamp)
        );
    }
}
