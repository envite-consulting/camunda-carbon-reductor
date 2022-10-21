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
