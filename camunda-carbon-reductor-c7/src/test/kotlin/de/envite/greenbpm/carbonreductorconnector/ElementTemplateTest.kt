package de.envite.greenbpm.carbonreductorconnector

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ProcessEngineExtension::class)
class ElementTemplateTest {
    private val processKey = "CarbonReductorTestProcess"
    private val carbonReductorId = "ST_Carbon_Reductor"

    @Test
    @Deployment(resources = ["test.bpmn"])
    fun test() {
        val processInstance = runtimeService().startProcessInstanceByKey(processKey)
        assertThat(processInstance).isStarted
        assertThat(processInstance).isWaitingAt(carbonReductorId)
        complete(
            externalTask(carbonReductorId), mapOf<String, Any>(
                "originalCarbon" to 100,
                "actualCarbon" to 50,
                "savedCarbon" to 50,
                "reducedCarbon" to 50,
                "executionDelayed" to true,
                "delayedBy" to 100000
            )
        )
        // input variables
        assertThat(processInstance).hasVariables(
            "remainingProcessDuration",
            "maximumProcessDuration",
            "carbonReductorMode",
            "location",
            "milestone"
        )
        // output variables
        assertThat(processInstance).hasVariables(
            "originalCarbonMapped",
            "actualCarbonMapped",
            "savedCarbonMapped",
            "reducedCarbonMapped",
            "executionDelayedMapped",
            "delayedByMapped"
        )
        assertThat(processInstance).hasVariables(
            "originalCarbon",
            "actualCarbon",
            "savedCarbon",
            "reducedCarbon",
            "executionDelayed",
            "delayedBy"
        )
        assertThat(processInstance).isEnded
    }
}