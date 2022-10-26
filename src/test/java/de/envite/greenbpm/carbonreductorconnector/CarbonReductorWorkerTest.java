package de.envite.greenbpm.carbonreductorconnector;

import static de.envite.greenbpm.carbonreductorconnector.service.Locations.NORWAY_EAST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.service.DelayCalculator;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class CarbonReductorWorkerTest {

  static CarbonReductorInput carbonReductorInput;
  static CarbonReductorOutput carbonReductorOutput_Clean;
  static CarbonReductorOutput carbonReductorOutput_Dirty;

  @BeforeAll
  static void init() {
    carbonReductorInput = new CarbonReductorInput();
    carbonReductorInput.setTimerDuration("PT5M");
    carbonReductorInput.setLocation(NORWAY_EAST.regionname());
    carbonReductorInput.setTimestamp("2022-10-20T11:35:45.826Z[Etc/UTC]");
    carbonReductorOutput_Clean =
        CarbonReductorOutput.builder()
            .originalCarbon(500.0)
            .actualCarbon(500.0)
            .savedCarbon(0.0)
            .executionDelayed(false)
            .delayedBy(0)
            .build();
    carbonReductorOutput_Dirty =
        CarbonReductorOutput.builder()
            .originalCarbon(500.0)
            .actualCarbon(250.0)
            .savedCarbon(250.0)
            .executionDelayed(true)
            .delayedBy(3000)
            .build();
  }

  @Test
  void shouldNotTimeShiftWhenEnergyIsClean() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);
    when(delayCalculator.calculateDelay(any())).thenReturn(carbonReductorOutput_Clean);

    var worker = new CarbonReductorWorker(delayCalculator);

    ActivatedJob job = mock(ActivatedJob.class);
    when(job.getVariables()).thenReturn("{\n" +
            "              \"timerDuration\": \"PT5M\",\n" +
            "              \"location\": \"norwayeast\",\n" +
            "              \"timestamp\": \"2022-10-20T11:35:45.826Z[Etc/UTC]\"\n" +
            "            }");
    JobClient client = mock(JobClient.class, RETURNS_DEEP_STUBS);
    when(job.getRetries()).thenReturn(3);
    when(client.newCompleteCommand(job).send()).thenReturn(mock(ZeebeFuture.class));
    worker.execute(client, job);

    verify(client, times(2)).newCompleteCommand(job);
  }

  @Test
  void shouldTimeShiftWhenEnergyIsDirty() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);
    when(delayCalculator.calculateDelay(any())).thenReturn(carbonReductorOutput_Dirty);

    var worker = new CarbonReductorWorker(delayCalculator);

    ActivatedJob job = mock(ActivatedJob.class);
    when(job.getVariables()).thenReturn("{\n" +
            "              \"timerDuration\": \"PT5M\",\n" +
            "              \"location\": \"norwayeast\",\n" +
            "              \"timestamp\": \"2022-10-20T11:35:45.826Z[Etc/UTC]\"\n" +
            "            }");
    JobClient client = mock(JobClient.class, RETURNS_DEEP_STUBS);
    when(job.getRetries()).thenReturn(3);
    when(client.newFailCommand(job).retries(999).retryBackoff(any(Duration.class)).send()).thenReturn(mock(ZeebeFuture.class));
    worker.execute(client, job);

    verify(client, times(2)).newFailCommand(job); //One interaction is already for the setup
  }

  @Test
  void shouldCompleteAfterTimeShift() throws Exception {
    DelayCalculator delayCalculator = mock(DelayCalculator.class);

    var worker = new CarbonReductorWorker(delayCalculator);

    ActivatedJob job = mock(ActivatedJob.class);
    JobClient client = mock(JobClient.class, RETURNS_DEEP_STUBS);

    when(job.getRetries()).thenReturn(999);
    when(client.newCompleteCommand(job).send()).thenReturn(mock(ZeebeFuture.class));
    worker.execute(client, job);

    verify(delayCalculator, never()).calculateDelay(any(CarbonReductorInput.class));
    verify(client, times(2)).newCompleteCommand(job);
  }
}
