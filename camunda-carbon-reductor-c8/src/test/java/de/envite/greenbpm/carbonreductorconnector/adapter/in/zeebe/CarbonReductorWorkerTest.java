package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;
import de.envite.greenbpm.carbonreductor.core.domain.service.DelayCalculatorService;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorInputVariable;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorOutputVariable;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorVariableMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.*;

import static de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils.TestDataGenerator.createInputVariables;
import static de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils.TestDataGenerator.createSLABasedCarbonReductorInput;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CarbonReductorWorkerTest {

    static CarbonReductorConfiguration carbonReductorConfiguration;
    static CarbonReduction carbonReductorOutput_Clean;
    static CarbonReduction carbonReductorOutput_Dirty;

    static CarbonReductorVariableMapper variableMapper;

    @BeforeAll
    static void init() {
        variableMapper = new CarbonReductorVariableMapper();
        OffsetDateTime timestamp = OffsetDateTime.of(
                LocalDate.of(2020, 7, 31),
                LocalTime.of(14, 27, 30),
                ZoneId.of("Europe/Berlin").getRules().getOffset(Instant.now())
        );
        carbonReductorConfiguration = createSLABasedCarbonReductorInput(timestamp);
        carbonReductorOutput_Clean = new CarbonReduction(
                new Delay(false, 0),
                new Carbon(500.0),
                new Carbon(500.0),
                new Percentage(0.0)
        );
        carbonReductorOutput_Dirty = new CarbonReduction(
                new Delay(true, 3000),
                new Carbon(500.0),
                new Carbon(500.0),
                new Percentage(250.0)
        );
    }

    @Test
    void shouldNotTimeShiftWhenEnergyIsClean() throws Exception {
        DelayCalculatorService delayCalculatorService = mock(DelayCalculatorService.class);
        when(delayCalculatorService.calculateDelay(any())).thenReturn(carbonReductorOutput_Clean);
        ZeebeClient client = mock(ZeebeClient.class, RETURNS_DEEP_STUBS);

        var worker = new CarbonReductorWorker(client, delayCalculatorService, variableMapper);

        ActivatedJob job = mock(ActivatedJob.class);
        when(job.getVariablesAsType(CarbonReductorInputVariable.class)).thenReturn(createInputVariables());

        when(job.getRetries()).thenReturn(3);
        when(client.newSetVariablesCommand(job.getElementInstanceKey()).variables(any(CarbonReductorOutputVariable.class)).send()).thenReturn(mock(ZeebeFuture.class));
        when(client.newCompleteCommand(job).send()).thenReturn(mock(ZeebeFuture.class));
        worker.execute(job);

        verify(client, times(2)).newSetVariablesCommand(anyLong());
        verify(client, times(2)).newCompleteCommand(job);
    }

    @Test
    void shouldTimeShiftWhenEnergyIsDirty() throws Exception {
        DelayCalculatorService delayCalculatorService = mock(DelayCalculatorService.class);
        when(delayCalculatorService.calculateDelay(any())).thenReturn(carbonReductorOutput_Dirty);
        ZeebeClient client = mock(ZeebeClient.class, RETURNS_DEEP_STUBS);

        var worker = new CarbonReductorWorker(client, delayCalculatorService, variableMapper);

        ActivatedJob job = mock(ActivatedJob.class);
        when(job.getVariablesAsType(CarbonReductorInputVariable.class)).thenReturn(createInputVariables());
        when(job.getRetries()).thenReturn(3);
        when(client.newSetVariablesCommand(job.getElementInstanceKey()).variables(any(CarbonReductorOutputVariable.class)).send()).thenReturn(mock(ZeebeFuture.class));
        when(client.newFailCommand(job).retries(999).retryBackoff(any(Duration.class)).send()).thenReturn(mock(ZeebeFuture.class));
        worker.execute(job);

        verify(client, times(2)).newSetVariablesCommand(job.getElementInstanceKey());
        verify(client, times(2)).newFailCommand(job); //One interaction is already for the setup
    }

    @Test
    void shouldCompleteAfterTimeShift() throws Exception {
        DelayCalculatorService delayCalculatorService = mock(DelayCalculatorService.class);
        ZeebeClient client = mock(ZeebeClient.class, RETURNS_DEEP_STUBS);

        var worker = new CarbonReductorWorker(client, delayCalculatorService, variableMapper);

        ActivatedJob job = mock(ActivatedJob.class);

        when(job.getRetries()).thenReturn(999);
        when(client.newSetVariablesCommand(job.getElementInstanceKey()).variables(anyLong()).send()).thenReturn(mock(ZeebeFuture.class));
        when(client.newCompleteCommand(job).send()).thenReturn(mock(ZeebeFuture.class));
        worker.execute(job);

        verify(delayCalculatorService, never()).calculateDelay(any(CarbonReductorConfiguration.class));
        verify(client, times(1)).newSetVariablesCommand(job.getElementInstanceKey());
        verify(client, times(2)).newCompleteCommand(job);
    }

    @Test
    void shouldThrowBPMNErrorOnCarbonReductorException() throws Exception {
        DelayCalculatorService delayCalculatorService = mock(DelayCalculatorService.class);
        when(delayCalculatorService.calculateDelay(any())).thenThrow(new CarbonReductorException("Test", new RuntimeException("Testing")));
        ZeebeClient client = mock(ZeebeClient.class, RETURNS_DEEP_STUBS);
        CarbonReductorOutputVariable defaultCarbonReductorOutput = createDefaultOutput();

        var worker = new CarbonReductorWorker(client, delayCalculatorService, variableMapper);

        ActivatedJob job = mock(ActivatedJob.class);
        when(job.getVariablesAsType(CarbonReductorInputVariable.class)).thenReturn(createInputVariables());

        when(job.getRetries()).thenReturn(3);
        worker.execute(job);

        verify(client.newThrowErrorCommand(job).errorCode("carbon-reductor-error")).errorMessage("Test");
        verify(client, times(1)).newSetVariablesCommand(job.getElementInstanceKey());
        verify(client.newSetVariablesCommand(job.getElementInstanceKey())).variables(defaultCarbonReductorOutput);
        verify(client, never()).newCompleteCommand(job);
    }

    @Test
    void shouldWriteDataAndNotShiftOnMeasurementOnly() throws Exception {
        DelayCalculatorService delayCalculatorService = mock(DelayCalculatorService.class);
        when(delayCalculatorService.calculateDelay(any())).thenReturn(carbonReductorOutput_Dirty);
        ZeebeClient client = mock(ZeebeClient.class, RETURNS_DEEP_STUBS);

        var worker = new CarbonReductorWorker(client, delayCalculatorService, variableMapper);

        ActivatedJob job = mock(ActivatedJob.class);
        CarbonReductorInputVariable inputVariables = createInputVariables();
        inputVariables.setMeasurementOnly(true);
        when(job.getVariablesAsType(CarbonReductorInputVariable.class)).thenReturn(inputVariables);
        when(job.getRetries()).thenReturn(3);
        when(client.newSetVariablesCommand(job.getElementInstanceKey()).variables(any(CarbonReductorOutputVariable.class)).send()).thenReturn(mock(ZeebeFuture.class));
        when(client.newCompleteCommand(job).send()).thenReturn(mock(ZeebeFuture.class));
        worker.execute(job);

        verify(client, times(2)).newSetVariablesCommand(job.getElementInstanceKey());
        verify(client, times(2)).newCompleteCommand(job);
    }

    private CarbonReductorOutputVariable createDefaultOutput() {
        CarbonReductorOutputVariable defaultCarbonReductorOutput = new CarbonReductorOutputVariable();
        defaultCarbonReductorOutput.setExecutionDelayed(false);
        defaultCarbonReductorOutput.setCarbonWithoutOptimization(0.0);
        defaultCarbonReductorOutput.setOptimalForecastedCarbon(0.0);
        defaultCarbonReductorOutput.setSavedCarbonPercentage(0.0);
        defaultCarbonReductorOutput.setCarbonReduction(0.0);
        defaultCarbonReductorOutput.setDelayedBy(0);
        return defaultCarbonReductorOutput;
    }
}