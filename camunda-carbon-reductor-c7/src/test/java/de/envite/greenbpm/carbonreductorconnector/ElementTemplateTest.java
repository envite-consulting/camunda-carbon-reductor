package de.envite.greenbpm.carbonreductorconnector;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

@ExtendWith(ProcessEngineExtension.class)
class ElementTemplateTest {
    private static final String PROCESS_KEY = "CarbonReductorTestProcess";
    private static final String CARBON_REDUCTOR_ID = "ST_Carbon_Reductor";

    @Test
    @Deployment(resources = "test.bpmn")
    void test() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_KEY);
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isWaitingAt(CARBON_REDUCTOR_ID);
        // input variables
        Map<String, Object> inputVariables = runtimeService().getVariables(externalTask().getExecutionId());
        assertThat(inputVariables).containsOnlyKeys(
                "remainingProcessDuration", "maximumProcessDuration", "location", "milestone", "errorHandling", "measurementOnly", "thresholdValue", "thresholdEnabled");
        complete(externalTask(CARBON_REDUCTOR_ID), Map.of(
                "originalCarbon", 100,
                "actualCarbon", 50,
                "savedCarbon", 50,
                "reducedCarbon", 50,
                "executionDelayed", true,
                "delayedBy", 100000));
        // output variables
        assertThat(processInstance).hasVariables(
                "originalCarbonMapped",
                "actualCarbonMapped",
                "savedCarbonMapped",
                "reducedCarbonMapped",
                "executionDelayedMapped",
                "delayedByMapped");
        assertThat(processInstance).hasVariables(
                "originalCarbon",
                "actualCarbon",
                "savedCarbon",
                "reducedCarbon",
                "executionDelayed",
                "delayedBy");
        assertThat(processInstance).isEnded();
    }
}
