package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorInputVariable;

import java.time.OffsetDateTime;

import static de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations.NORWAY_EAST;

public class TestDataGenerator {

    public static CarbonReductorInputVariable createInputVariables() {
        CarbonReductorInputVariable inputVariable = new CarbonReductorInputVariable();
        inputVariable.setLocation("norwayeast");
        inputVariable.setMilestone("2020-07-31T14:27:30.766Z[GMT]");
        inputVariable.setRemainingProcessDuration("PT10M");
        inputVariable.setTimeshiftWindow("PT6H");
        return inputVariable;
    }

    public static CarbonReduction createCarbonReductorOutput() {
        return new CarbonReduction(
                new Delay(true, 3),
                new Carbon(1.0),
                new Carbon(2.0),
                new Percentage(3.0)
        );
    }

    public static CarbonReductorConfiguration createSLABasedCarbonReductorInput(OffsetDateTime timestamp) {
        return new CarbonReductorConfiguration(
                NORWAY_EAST.asLocation(),
                new Milestone(timestamp),
                new Timeshift("PT5H"),
                new Timeshift("PT10H"),
                null,
                null,
                false, threshold);
    }
}
