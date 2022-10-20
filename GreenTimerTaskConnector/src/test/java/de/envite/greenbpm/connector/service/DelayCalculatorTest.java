/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.envite.greenbpm.connector.service;

import static de.envite.greenbpm.connector.service.Locations.GERMANY_WEST_CENTRAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.envite.greenbpm.connector.model.GreenEnergyInput;
import de.envite.greenbpm.connector.model.GreenEnergyOutput;
import io.swagger.client.model.EmissionsData;
import io.swagger.client.model.EmissionsDataDTO;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

class DelayCalculatorTest {

  private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC =
      "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

  private GreenEnergyInput greenEnergyInput_DelayedWorkerExecution;
  private GreenEnergyInput greenEnergyInput_ImmediateWorkerExecution;
  private EmissionsData emissionData;

  @BeforeEach
  void init() {
    greenEnergyInput_DelayedWorkerExecution = new GreenEnergyInput();
    greenEnergyInput_DelayedWorkerExecution.setTimerDuration("PT5M");
    greenEnergyInput_DelayedWorkerExecution.setLocation(GERMANY_WEST_CENTRAL.name());
    OffsetDateTime timestampDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(1);
    greenEnergyInput_DelayedWorkerExecution.setTimestamp(
        timestampDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
    greenEnergyInput_ImmediateWorkerExecution = new GreenEnergyInput();
    greenEnergyInput_ImmediateWorkerExecution.setTimerDuration("PT5M");
    greenEnergyInput_ImmediateWorkerExecution.setLocation(GERMANY_WEST_CENTRAL.name());
    OffsetDateTime timestampImmediate = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
    greenEnergyInput_ImmediateWorkerExecution.setTimestamp(
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
    GreenEnergyOutput greenEnergyOutput =
        delayCalculator.calculateDelay(greenEnergyInput_DelayedWorkerExecution);

    assertThat(greenEnergyOutput.isExecutionDelayed()).isFalse();
    assertThat(greenEnergyOutput.getDelayedBy()).isZero();
    assertThat(greenEnergyOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getSavedCarbon()).isZero();
  }

  @Test
  void shouldCalculateNoDelayBecauseNotNecessary() throws CarbonAwareSDKException {
    CarbonAwareSDKService service = mock(CarbonAwareSDKService.class);
    when(service.getCurrentEmission(any())).thenReturn(emissionData);
    EmissionsDataDTO emissionDataForecast = new EmissionsDataDTO().value(500.1);
    when(service.getOptimalForecastUntil(any(), any())).thenReturn(emissionDataForecast);

    DelayCalculator delayCalculator = new DelayCalculator(service);
    GreenEnergyOutput greenEnergyOutput =
        delayCalculator.calculateDelay(greenEnergyInput_ImmediateWorkerExecution);

    assertThat(greenEnergyOutput.isExecutionDelayed()).isFalse();
    assertThat(greenEnergyOutput.getDelayedBy()).isZero();
    assertThat(greenEnergyOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getSavedCarbon()).isZero();
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
    GreenEnergyOutput greenEnergyOutput =
        delayCalculator.calculateDelay(greenEnergyInput_ImmediateWorkerExecution);

    assertThat(greenEnergyOutput.isExecutionDelayed()).isTrue();
    assertThat(greenEnergyOutput.getDelayedBy())
        .isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
    assertThat(greenEnergyOutput.getActualCarbon()).isEqualTo(200.6);
    assertThat(greenEnergyOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getSavedCarbon()).isEqualTo(299.5);
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
    GreenEnergyOutput greenEnergyOutput =
        delayCalculator.calculateDelay(greenEnergyInput_DelayedWorkerExecution);

    assertThat(greenEnergyOutput.isExecutionDelayed()).isFalse();
    assertThat(greenEnergyOutput.getDelayedBy()).isZero();
    assertThat(greenEnergyOutput.getActualCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getOriginalCarbon()).isEqualTo(500.1);
    assertThat(greenEnergyOutput.getSavedCarbon()).isZero();
  }
}
