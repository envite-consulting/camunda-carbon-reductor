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
package de.envite.greenbpm.connector;

import static de.envite.greenbpm.connector.service.Locations.NORWAY_EAST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.envite.greenbpm.connector.model.GreenEnergyInput;
import de.envite.greenbpm.connector.model.GreenEnergyOutput;
import de.envite.greenbpm.connector.service.DelayCalculator;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DelayForGreenEnergyFunctionTest {

  static GreenEnergyInput greenEnergyInput;
  static GreenEnergyOutput greenEnergyOutput_NoDelay;
  static GreenEnergyOutput greenEnergyOutput_WithDelay;

  @BeforeAll
  static void init() {
    greenEnergyInput = new GreenEnergyInput();
    greenEnergyInput.setTimerDuration("PT5M");
    greenEnergyInput.setLocation(NORWAY_EAST.regionname());
    greenEnergyInput.setTimestamp("2022-10-20T11:35:45.826Z[Etc/UTC]");
    greenEnergyOutput_NoDelay =
        GreenEnergyOutput.builder()
            .originalCarbon(500.0)
            .actualCarbon(500.0)
            .savedCarbon(0.0)
            .executionDelayed(false)
            .delayedBy(0)
            .build();
    greenEnergyOutput_WithDelay =
        GreenEnergyOutput.builder()
            .originalCarbon(500.0)
            .actualCarbon(250.0)
            .savedCarbon(250.0)
            .executionDelayed(true)
            .delayedBy(3000)
            .build();
  }

  @Test
  void shouldNotDelay() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);
    when(delayCalculator.calculateDelay(any())).thenReturn(greenEnergyOutput_NoDelay);

    var function = new DelayForGreenEnergyFunction(delayCalculator);
    var context = OutboundConnectorContextBuilder.create().variables(greenEnergyInput).build();

    GreenEnergyOutput output = (GreenEnergyOutput) function.execute(context);
    assertThat(output).isNotNull();
  }

  @Test
  void shouldDelay() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);
    when(delayCalculator.calculateDelay(any())).thenReturn(greenEnergyOutput_WithDelay);

    var function = new DelayForGreenEnergyFunction(delayCalculator);
    var context = OutboundConnectorContextBuilder.create().variables(greenEnergyInput).build();

    GreenEnergyOutput output = (GreenEnergyOutput) function.execute(context);
    assertThat(output).isNotNull();
  }
}
