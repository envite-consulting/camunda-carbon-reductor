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
package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.service.CarbonAwareSDKException;
import de.envite.greenbpm.carbonreductorconnector.service.DelayCalculator;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import lombok.extern.slf4j.Slf4j;

@OutboundConnector(
    name = "CarbonReductorTask",
    inputVariables = {"timerDuration", "location", "timestamp"},
    type = "de.envite.greenbpm.carbonreductorconnector.carbonreductortask:1")
@Slf4j
public class CarbonReductorConnectorFunction implements OutboundConnectorFunction {

  private final DelayCalculator delayCalculator;

  public CarbonReductorConnectorFunction() {
    this(new DelayCalculator());
  }

  public CarbonReductorConnectorFunction(DelayCalculator delayCalculator) {
    this.delayCalculator = delayCalculator;
  }

  @Override
  public Object execute(OutboundConnectorContext context)
      throws CarbonAwareSDKException, InterruptedException {
    final var variables = context.getVariablesAsType(CarbonReductorInput.class);
    context.validate(variables);

    log.info("input {}", variables);

    CarbonReductorOutput carbonReductorOutput = delayCalculator.calculateDelay(variables);

    log.info("CarbonReductorOutput {}", carbonReductorOutput);

    if (carbonReductorOutput.isExecutionDelayed()) {
      log.info("delay execution");
      Thread.sleep(carbonReductorOutput.getDelayedBy());
      log.info("restart execution...");
    } else {
      log.info("no delay necessary");
    }

    return carbonReductorOutput;
  }
}
