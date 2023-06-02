package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Getter
public class CarbonReductorConfiguration extends Aggregate {

    private final Location location;
    private final Milestone milestone;
    private final Timeshift remainingProcessTimeshift;
    private final Timeshift maximumProcessTimeshift;
    private final Timeshift timeshiftWindow;
    private final ExceptionHandlingEnum exceptionHandling;

    private static final ExceptionHandlingEnum EXCEPTION_HANDLING_DEFAULT = ExceptionHandlingEnum.CONTINUE_ON_EXCEPTION;

    public CarbonReductorConfiguration(Location location,
                                       Milestone milestone,
                                       Timeshift remainingProcessTimeshift,
                                       Timeshift maximumProcessTimeshift,
                                       Timeshift timeshiftWindow,
                                       ExceptionHandlingEnum exceptionHandling) {
        this.location = location;
        this.milestone = milestone;
        this.remainingProcessTimeshift = remainingProcessTimeshift;
        this.maximumProcessTimeshift = maximumProcessTimeshift;
        this.timeshiftWindow = timeshiftWindow;
        this.exceptionHandling = exceptionHandling == null ? EXCEPTION_HANDLING_DEFAULT : exceptionHandling;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(location, "Location");
        validateNotNull(milestone, "Milestone");
        validateNotNull(remainingProcessTimeshift, "Remaining Process Duration");
        evaluateValidations();
    }

    public boolean isDelayStillRelevant() {
        OffsetDateTime milestoneTimestamp = milestone.asDate();

        return (milestoneTimestamp
                .plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)
                .plus(maximumProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)
                .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessTimeshift.getValue().toMillis(), ChronoUnit.MILLIS)));
    }
}
