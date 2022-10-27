package de.envite.greenbpm.carbonreductorconnector.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.envite.greenbpm.carbonreductorconnector.domain.model.location.Location;
import io.github.domainprimitives.object.Aggregate;
import lombok.Getter;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

@Getter
public class CarbonReductorInput extends Aggregate {

  private final Duration duration;
  private final Location location;
  private final ExeceutionTimestamp timestamp;

  @JsonCreator
  public CarbonReductorInput(@JsonProperty("timerDuration") Duration duration, @JsonProperty("location") Location location, @JsonProperty("timestamp") ExeceutionTimestamp timestamp) {
    this.duration = duration;
    this.location = location;
    this.timestamp = timestamp;
    this.validate();
  }

  @Override
  protected void validate() {
    validateNotNull(duration, "Duration");
    validateNotNull(location, "Location");
    validateNotNull(timestamp, "Execeution Timestamp");
    evaluateValidations();
  }

  public boolean isDelayStillRelevant() {
    OffsetDateTime jobTimestamp = timestamp.asDate();
    return (jobTimestamp
            .plus(duration.asDuration(), ChronoUnit.MILLIS)
            .isAfter(OffsetDateTime.now(ZoneOffset.UTC)));
  }
}
