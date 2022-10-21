package de.envite.greenbpm.carbonreductorconnector.service;

import static de.envite.greenbpm.carbonreductorconnector.service.Locations.GERMANY_WEST_CENTRAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorOutput;
import io.swagger.client.model.EmissionsData;
import io.swagger.client.model.EmissionsDataDTO;
import java.time.Duration;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

class DelayCalculatorTest {

  private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC =
      "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

  private CarbonReductorInput carbonReductorInput_DelayedWorkerExecution;
  private CarbonReductorInput carbonReductorInput_ImmediateWorkerExecution;
  private EmissionsData emissionData;

  @BeforeEach
  void init() {
    carbonReductorInput_DelayedWorkerExecution = new CarbonReductorInput();
    carbonReductorInput_DelayedWorkerExecution.setTimerDuration("PT5M");
    carbonReductorInput_DelayedWorkerExecution.setLocation(GERMANY_WEST_CENTRAL.name());
    OffsetDateTime timestampDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(1);
    carbonReductorInput_DelayedWorkerExecution.setTimestamp(
        timestampDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
    carbonReductorInput_ImmediateWorkerExecution = new CarbonReductorInput();
    carbonReductorInput_ImmediateWorkerExecution.setTimerDuration("PT5M");
    carbonReductorInput_ImmediateWorkerExecution.setLocation(GERMANY_WEST_CENTRAL.name());
    OffsetDateTime timestampImmediate = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
    carbonReductorInput_ImmediateWorkerExecution.setTimestamp(
        timestampImmediate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
    emissionData = new EmissionsData().rating(500.1);
  }

  @Test
  void shouldCalculateNoDelayBecauseNoLongerRelevant() throws CarbonAwareSDKException {
    CarbonAwareSDKService service = mock(CarbonAwareSDKService.class);
    when(service.getCurrentEmission(any())).thenReturn(emissionData);
    EmissionsDataDTO emissionDataForecast = new EmissionsDataDTO().value(500.1);
    when(service.getOptimalForecastUntil(any(), any())).thenReturn(emissionDataForecast);

    DelayCalculator delayCalculator = new DelayCalculator(service);
    CarbonReductorOutput carbonReductorOutput =
        delayCalculator.calculateDelay(carbonReductorInput_DelayedWorkerExecution);

    assertThat(carbonReductorOutput.isExecutionDelayed()).isFalse();
    assertThat(carbonReductorOutput.getDelayedBy()).isZero();
    assertThat(carbonReductorOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getSavedCarbon()).isZero();
  }

  @Test
  void shouldCalculateNoDelayBecauseNotNecessary() throws CarbonAwareSDKException {
    CarbonAwareSDKService service = mock(CarbonAwareSDKService.class);
    when(service.getCurrentEmission(any())).thenReturn(emissionData);
    EmissionsDataDTO emissionDataForecast = new EmissionsDataDTO().value(500.1);
    when(service.getOptimalForecastUntil(any(), any())).thenReturn(emissionDataForecast);

    DelayCalculator delayCalculator = new DelayCalculator(service);
    CarbonReductorOutput carbonReductorOutput =
        delayCalculator.calculateDelay(carbonReductorInput_ImmediateWorkerExecution);

    assertThat(carbonReductorOutput.isExecutionDelayed()).isFalse();
    assertThat(carbonReductorOutput.getDelayedBy()).isZero();
    assertThat(carbonReductorOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getSavedCarbon()).isZero();
  }

  @Test
  void shouldCalculateDelayBecauseImmidiateWorkerExecution() throws CarbonAwareSDKException {
    CarbonAwareSDKService service = mock(CarbonAwareSDKService.class);
    when(service.getCurrentEmission(any())).thenReturn(emissionData);
    EmissionsDataDTO emissionDataForecast =
        new EmissionsDataDTO()
            .value(200.6)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC).plusHours(3));
    when(service.getOptimalForecastUntil(any(), any())).thenReturn(emissionDataForecast);

    DelayCalculator delayCalculator = new DelayCalculator(service);
    CarbonReductorOutput carbonReductorOutput =
        delayCalculator.calculateDelay(carbonReductorInput_ImmediateWorkerExecution);

    assertThat(carbonReductorOutput.isExecutionDelayed()).isTrue();
    assertThat(carbonReductorOutput.getDelayedBy())
        .isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
    assertThat(carbonReductorOutput.getActualCarbon()).isEqualTo(200.6);
    assertThat(carbonReductorOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getSavedCarbon()).isCloseTo(59.88, offset(0.1));
  }

  @Test
  void shouldCalculateNoDelayBecauseDelayedWorkerExecution() throws CarbonAwareSDKException {
    CarbonAwareSDKService service = mock(CarbonAwareSDKService.class);
    when(service.getCurrentEmission(any())).thenReturn(emissionData);
    EmissionsDataDTO emissionDataForecast =
        new EmissionsDataDTO()
            .value(200.6)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC).plusHours(3));
    when(service.getOptimalForecastUntil(any(), any())).thenReturn(emissionDataForecast);

    DelayCalculator delayCalculator = new DelayCalculator(service);
    CarbonReductorOutput carbonReductorOutput =
        delayCalculator.calculateDelay(carbonReductorInput_DelayedWorkerExecution);

    assertThat(carbonReductorOutput.isExecutionDelayed()).isFalse();
    assertThat(carbonReductorOutput.getDelayedBy()).isZero();
    assertThat(carbonReductorOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(carbonReductorOutput.getSavedCarbon()).isZero();
  }
}
