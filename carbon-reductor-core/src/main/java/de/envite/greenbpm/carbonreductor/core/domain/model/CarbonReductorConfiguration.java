package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Threshold;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
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
    private final ProcessDuration remainingProcessDuration;
    private final ProcessDuration maximumProcessDuration;
    private final ExceptionHandlingEnum exceptionHandling;
    private final boolean measurementOnly;
    private final Threshold threshold;

    private static final ExceptionHandlingEnum EXCEPTION_HANDLING_DEFAULT = ExceptionHandlingEnum.CONTINUE_ON_EXCEPTION;

    public CarbonReductorConfiguration(Location location,
                                       Milestone milestone,
                                       ProcessDuration remainingProcessDuration,
                                       ProcessDuration maximumProcessDuration,
                                       ExceptionHandlingEnum exceptionHandling,
                                       boolean measurementOnly,
                                       Threshold threshold) {
        this.location = location;
        this.milestone = milestone;
        this.remainingProcessDuration = remainingProcessDuration;
        this.maximumProcessDuration = maximumProcessDuration;
        this.exceptionHandling = exceptionHandling == null ? EXCEPTION_HANDLING_DEFAULT : exceptionHandling;
        this.measurementOnly = measurementOnly;
        this.threshold = threshold;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(location, "Location");
        validateNotNull(milestone, "Milestone");
        validateNotNull(remainingProcessDuration, "Remaining Process Duration");
        evaluateValidations();
    }

    public boolean isDelayStillRelevant() {
        OffsetDateTime milestoneTimestamp = milestone.asDate();

        return (milestoneTimestamp
                .plus(remainingProcessDuration.getValue().toMillis(), ChronoUnit.MILLIS)
                .plus(maximumProcessDuration.getValue().toMillis(), ChronoUnit.MILLIS)
                .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessDuration.getValue().toMillis(), ChronoUnit.MILLIS)));
    }
}
