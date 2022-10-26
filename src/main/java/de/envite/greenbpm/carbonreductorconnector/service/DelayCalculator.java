package de.envite.greenbpm.carbonreductorconnector.service;

import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorOutput;
import io.swagger.client.model.EmissionsData;
import io.swagger.client.model.EmissionsDataDTO;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

@Slf4j
public class DelayCalculator {

  private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC =
      "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";
  private final CarbonAwareSDKService service;

  public DelayCalculator() {
    this(new CarbonAwareSDKService());
  }

  public DelayCalculator(CarbonAwareSDKService service) {
    this.service = service;
  }

  public CarbonReductorOutput calculateDelay(CarbonReductorInput input) throws CarbonAwareSDKException {
    EmissionsData currentEmission =
        service.getCurrentEmission(Locations.valueOf(input.getLocation()));
    EmissionsDataDTO forecastedOptimalTime =
        service.getOptimalForecastUntil(
            Locations.valueOf(input.getLocation()),
            calculateUntilDateTime(input.getTimerDuration()));

    if (isDelayNecessary(input, currentEmission, forecastedOptimalTime)) {
      long optimalTime = forecastedOptimalTime.getTimestamp().toInstant().toEpochMilli();
      long delayedBy = optimalTime - OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
      return CarbonReductorOutput.builder()
          .executionDelayed(true)
          .delayedBy(delayedBy)
          .originalCarbon(currentEmission.getRating())
          .actualCarbon(forecastedOptimalTime.getValue())
          .savedCarbon(calculateSavedCarbonPercentage(currentEmission, forecastedOptimalTime))
          .build();
    }
    // execution is optimal currently
    return CarbonReductorOutput.builder()
        .executionDelayed(false)
        .delayedBy(0)
        .originalCarbon(currentEmission.getRating())
        .actualCarbon(currentEmission.getRating())
        .savedCarbon(0.0)
        .build();
  }

  private boolean isDelayNecessary(
      CarbonReductorInput input,
      EmissionsData currentEmission,
      EmissionsDataDTO forecastedOptimalTime) {
    return isDelayStillRelevant(input.getTimestamp(), input.getTimerDuration())
        && isCleanerEnergyInFuture(currentEmission, forecastedOptimalTime);
  }

  private boolean isDelayStillRelevant(String jobTimestampString, String durationString) {
    OffsetDateTime jobTimestamp =
        OffsetDateTime.parse(
            jobTimestampString, DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC));
    return (jobTimestamp
        .plus(parseDuration(durationString), ChronoUnit.MILLIS)
        .isAfter(OffsetDateTime.now(ZoneOffset.UTC)));
  }

  private boolean isCleanerEnergyInFuture(
    EmissionsData currentEmission, EmissionsDataDTO forecastedOptimalTime) {
    return currentEmission.getRating() > forecastedOptimalTime.getValue();
  }

  private double calculateSavedCarbonPercentage(EmissionsData currentEmission, EmissionsDataDTO forecastedOptimalTime) {
    double difference = currentEmission.getRating() - forecastedOptimalTime.getValue();
    return (difference / currentEmission.getRating()) * 100;
  }

  private OffsetDateTime calculateUntilDateTime(String durationString) {
    long timerDurationMillis = parseDuration(durationString);
    return OffsetDateTime.now(ZoneOffset.UTC).plus(timerDurationMillis, ChronoUnit.MILLIS);
  }

  private long parseDuration(String durationString) {
    Duration duration = Duration.parse(durationString);
    return duration.toMillis();
  }
}
