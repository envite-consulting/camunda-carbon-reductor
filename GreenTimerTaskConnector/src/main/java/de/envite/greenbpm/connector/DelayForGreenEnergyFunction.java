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

import de.envite.greenbpm.connector.model.GreenEnergyInput;
import de.envite.greenbpm.connector.model.GreenEnergyOutput;
import de.envite.greenbpm.connector.service.CarbonAwareSDKException;
import de.envite.greenbpm.connector.service.DelayCalculator;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import lombok.extern.slf4j.Slf4j;

@OutboundConnector(
    name = "DelayForGreenEnergyTask",
    inputVariables = {"timerDuration", "location", "timestamp"},
    type = "de.envite.greenbpm.connectors.delayforgreenenergytask:1")
@Slf4j
public class DelayForGreenEnergyFunction implements OutboundConnectorFunction {

  private final DelayCalculator delayCalculator;

  public DelayForGreenEnergyFunction() {
    this(new DelayCalculator());
  }

  public DelayForGreenEnergyFunction(DelayCalculator delayCalculator) {
    this.delayCalculator = delayCalculator;
  }

  @Override
  public Object execute(OutboundConnectorContext context)
      throws CarbonAwareSDKException, InterruptedException {
    final var variables = context.getVariablesAsType(GreenEnergyInput.class);
    context.validate(variables);

    log.info("input {}", variables);

    GreenEnergyOutput greenEnergyOutput = delayCalculator.calculateDelay(variables);

    log.info("GreenTimerOutput {}", greenEnergyOutput);

    if (greenEnergyOutput.isExecutionDelayed()) {
      log.info("delay execution");
      Thread.sleep(greenEnergyOutput.getDelayedBy());
      log.info("restart execution...");
    } else {
      log.info("no delay necessary");
    }

    return greenEnergyOutput;
  }
}
