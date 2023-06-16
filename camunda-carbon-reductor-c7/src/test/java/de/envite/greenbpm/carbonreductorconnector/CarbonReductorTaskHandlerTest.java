package de.envite.greenbpm.carbonreductorconnector;


import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Map;

import static de.envite.greenbpm.carbonreductorconnector.CarbonReductorTaskHandler.RETRIES_MAGIC_NUMBER;
import static org.mockito.Mockito.*;

@MockitoSettings
class CarbonReductorTaskHandlerTest {

    private CarbonReductorTaskHandler classUnderTest;

    private final DelayCalculator delayCalculatorMock = mock(DelayCalculator.class);

    private final CarbonReductorVariableMapper variableMapperMock = mock(CarbonReductorVariableMapper.class);

    @BeforeEach
    void setUp() {
        classUnderTest = new CarbonReductorTaskHandler(delayCalculatorMock, variableMapperMock);
    }

    @Test
    void should_query_reduction_write_variables_and_set_retry_if_delay() throws Exception {
        ExternalTask externalTask = mock(ExternalTask.class);
        when(externalTask.getRetries()).thenReturn(RETRIES_MAGIC_NUMBER - 1);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);
        CarbonReductorConfiguration carbonReductorConfiguration = mock(CarbonReductorConfiguration.class);
        when(variableMapperMock.mapToDomain(externalTask.getAllVariables())).thenReturn(carbonReductorConfiguration);
        long duration = 100000L;
        CarbonReduction carbonReductorOutput = mock(CarbonReduction.class, RETURNS_DEEP_STUBS);
        when(carbonReductorOutput.getDelay().isExecutionDelayed()).thenReturn(true);
        when(carbonReductorOutput.getDelay().getDelayedBy()).thenReturn(duration);
        when(delayCalculatorMock.calculateDelay(carbonReductorConfiguration)).thenReturn(carbonReductorOutput);
        Map<String, Object> outputVariables = Map.of();
        when(variableMapperMock.mapFromDomain(carbonReductorOutput, externalTask.getAllVariables())).thenReturn(outputVariables);

        classUnderTest.execute(externalTask, externalTaskService);

        verify(externalTaskService).handleFailure(
                externalTask.getId(),
                "Time shifting execution",
                "Time shifting execution due for a lower carbon footprint",
                RETRIES_MAGIC_NUMBER,
                duration,
                outputVariables,
                null);
    }

    @Test
    void should_query_reduction_write_variables_and_complete_if_no_delay() throws Exception {
        ExternalTask externalTask = mock(ExternalTask.class);
        when(externalTask.getRetries()).thenReturn(RETRIES_MAGIC_NUMBER - 1);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);
        CarbonReductorConfiguration carbonReductorConfiguration = mock(CarbonReductorConfiguration.class);
        when(variableMapperMock.mapToDomain(externalTask.getAllVariables())).thenReturn(carbonReductorConfiguration);
        CarbonReduction carbonReductorOutput = mock(CarbonReduction.class, RETURNS_DEEP_STUBS);
        when(carbonReductorOutput.getDelay().isExecutionDelayed()).thenReturn(false);
        when(delayCalculatorMock.calculateDelay(carbonReductorConfiguration)).thenReturn(carbonReductorOutput);
        Map<String, Object> outputVariables = Map.of();
        when(variableMapperMock.mapFromDomain(carbonReductorOutput, externalTask.getAllVariables())).thenReturn(outputVariables);

        classUnderTest.execute(externalTask, externalTaskService);

        verify(externalTaskService).complete(externalTask, outputVariables);
    }

    @Test
    void should_complete_if_max_limit_is_reached() {
        ExternalTask externalTask = mock(ExternalTask.class);
        when(externalTask.getRetries()).thenReturn(RETRIES_MAGIC_NUMBER);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);

        classUnderTest.execute(externalTask, externalTaskService);

        verify(externalTaskService).complete(externalTask);
        verifyNoInteractions(variableMapperMock);
        verifyNoInteractions(delayCalculatorMock);
    }

    @Test
    void should_throw_bpmn_error_on_CarbonReductorException_and_write_variables() throws Exception {
        ExternalTask externalTask = mock(ExternalTask.class);
        when(externalTask.getRetries()).thenReturn(RETRIES_MAGIC_NUMBER - 1);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);
        CarbonReductorConfiguration carbonReductorConfiguration = mock(CarbonReductorConfiguration.class);
        when(variableMapperMock.mapToDomain(externalTask.getAllVariables())).thenReturn(carbonReductorConfiguration);
        when(delayCalculatorMock.calculateDelay(carbonReductorConfiguration)).thenThrow(new CarbonReductorException("Test", new RuntimeException("Testing ...")));
        CarbonReduction defaultCarbonReductorOutput = new CarbonReduction(new Delay(false, 0L), new Carbon(0.0), new Carbon(0.0), new Carbon(0.0));
        Map<String, Object> outputVariables = Map.of();
        when(variableMapperMock.mapFromDomain(defaultCarbonReductorOutput, externalTask.getAllVariables())).thenReturn(outputVariables);

        classUnderTest.execute(externalTask, externalTaskService);

        verify(externalTaskService).handleBpmnError(
                externalTask,
                "carbon-reductor-error",
                "Test",
                outputVariables
        );
    }
}