package de.envite.greenbpm.carbonreductorconnector;

import static de.envite.greenbpm.carbonreductorconnector.service.Locations.NORWAY_EAST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.service.DelayCalculator;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CarbonReductorConnectorFunctionTest {

  static CarbonReductorInput carbonReductorInput;
  static CarbonReductorOutput carbonReductorOutput_NoDelay;
  static CarbonReductorOutput carbonReductorOutput_WithDelay;

  @BeforeAll
  static void init() {
    carbonReductorInput = new CarbonReductorInput();
    carbonReductorInput.setTimerDuration("PT5M");
    carbonReductorInput.setLocation(NORWAY_EAST.regionname());
    carbonReductorInput.setTimestamp("2022-10-20T11:35:45.826Z[Etc/UTC]");
    carbonReductorOutput_NoDelay =
        CarbonReductorOutput.builder()
            .originalCarbon(500.0)
            .actualCarbon(500.0)
            .savedCarbon(0.0)
            .executionDelayed(false)
            .delayedBy(0)
            .build();
    carbonReductorOutput_WithDelay =
        CarbonReductorOutput.builder()
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
    when(delayCalculator.calculateDelay(any())).thenReturn(carbonReductorOutput_NoDelay);

    var function = new CarbonReductorConnectorFunction(delayCalculator);
    var context = OutboundConnectorContextBuilder.create().variables(carbonReductorInput).build();

    CarbonReductorOutput output = (CarbonReductorOutput) function.execute(context);
    assertThat(output).isNotNull();
  }

  @Test
  void shouldDelay() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);
    when(delayCalculator.calculateDelay(any())).thenReturn(carbonReductorOutput_WithDelay);

    var function = new CarbonReductorConnectorFunction(delayCalculator);
    var context = OutboundConnectorContextBuilder.create().variables(carbonReductorInput).build();

    CarbonReductorOutput output = (CarbonReductorOutput) function.execute(context);
    assertThat(output).isNotNull();
  }
}
