package de.envite.greenbpm.carbonreductorconnector.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.envite.greenbpm.carbonreductorconnector.domain.model.carbonreductormode.CarbonReductorMode;
import de.envite.greenbpm.carbonreductorconnector.domain.model.location.Location;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.carbonreductormode.CarbonReductorModes.TIMESHIFT_WINDOW_ONLY;

@Getter
public class CarbonReductorInput extends Aggregate {

    private final Location location;
    private final CarbonReductorMode carbonReductorMode;
    private final ExecutionTimestamp milestone;
    private final Duration remainingProcessDuration;
    private final Duration maximumProcessDuration;
    private final Duration timeshiftWindow;

    @JsonCreator
    public CarbonReductorInput(@JsonProperty("location") Location location,
                               @JsonProperty("carbonReductorMode") CarbonReductorMode carbonReductorMode,
                               @JsonProperty("milestone") ExecutionTimestamp milestone,
                               @JsonProperty("remainingProcessDuration") Duration remainingProcessDuration,
                               @JsonProperty("maximumProcessDuration") Duration maximumProcessDuration,
                               @JsonProperty("timeshiftWindow") Duration timeshiftWindow) {
        this.location = location;
        this.carbonReductorMode = carbonReductorMode;
        this.milestone = milestone;
        this.remainingProcessDuration = remainingProcessDuration;
        this.maximumProcessDuration = maximumProcessDuration;
        this.timeshiftWindow = timeshiftWindow;
        this.validate();
    }

    @Override
    protected void validate() {
        validateNotNull(location, "Location");
        validateNotNull(carbonReductorMode, "CarbonReductorMode");
        validateNotNull(milestone, "Milestone");
        validateNotNull(remainingProcessDuration, "Remaining Process Duration");
        evaluateValidations();
    }

    public boolean isDelayStillRelevant() {
        OffsetDateTime milestoneTimestamp = milestone.asDate();

        if (TIMESHIFT_WINDOW_ONLY.mode().equals(carbonReductorMode.getValue())) {
            return (milestoneTimestamp
                    .plus(timeshiftWindow.asDuration(), ChronoUnit.MILLIS)
                    .plus(remainingProcessDuration.asDuration(), ChronoUnit.MILLIS)
                    .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessDuration.asDuration(), ChronoUnit.MILLIS))
                        &&
                    (remainingProcessDuration.asDuration() < timeshiftWindow.asDuration()));
        }
        return (milestoneTimestamp
                .plus(remainingProcessDuration.asDuration(), ChronoUnit.MILLIS)
                .plus(maximumProcessDuration.asDuration(), ChronoUnit.MILLIS)
                .isAfter(OffsetDateTime.now(ZoneOffset.UTC).plus(remainingProcessDuration.asDuration(), ChronoUnit.MILLIS)));
    }
}
