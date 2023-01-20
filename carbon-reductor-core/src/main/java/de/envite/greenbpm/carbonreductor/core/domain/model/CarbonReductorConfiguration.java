package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorMode;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorModes;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Getter
public class CarbonReductorConfiguration extends Aggregate {

    private final Location location;
    private final CarbonReductorMode carbonReductorMode;
    private final Milestone milestone;
    private final Timeshift remainingProcessTimeshift;
    private final Timeshift maximumProcessTimeshift;
    private final Timeshift timeshiftWindow;

    public CarbonReductorConfiguration(Location location,
                                       CarbonReductorMode carbonReductorMode,
                                       Milestone milestone,
                                       Timeshift remainingProcessTimeshift,
                                       Timeshift maximumProcessTimeshift,
                                       Timeshift timeshiftWindow) {
        this.location = location;
        this.carbonReductorMode = carbonReductorMode;
        this.milestone = milestone;
        this.remainingProcessTimeshift = remainingProcessTimeshift;
        this.maximumProcessTimeshift = maximumProcessTimeshift;
        this.timeshiftWindow = timeshiftWindow;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(location, "Location");
        validateNotNull(carbonReductorMode, "CarbonReductorMode");
        validateNotNull(milestone, "Milestone");
        validateNotNull(remainingProcessTimeshift, "Remaining Process Duration");
        evaluateValidations();
    }

    public boolean isDelayStillRelevant() {
        OffsetDateTime milestoneTimestamp = milestone.asDate();

        if (CarbonReductorModes.TIMESHIFT_WINDOW_ONLY.mode().equals(carbonReductorMode.getValue())) {
            return (milestoneTimestamp
                    .plus(timeshiftWindow.getValue().toMillis(), ChronoUnit.MILLIS)
                    .plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)
                    .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS))
                        &&
                    (remainingProcessTimeshift.getValue().toMillis() < timeshiftWindow.getValue().toMillis()));
        }
        return (milestoneTimestamp
                .plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)
                .plus(maximumProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)
                .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)));
    }
}
