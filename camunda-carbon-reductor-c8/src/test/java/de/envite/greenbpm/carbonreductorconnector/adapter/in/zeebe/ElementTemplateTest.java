package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.command.DeployResourceCommandStep1;
import io.camunda.client.api.response.ActivateJobsResponse;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.response.CompleteJobResponse;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaProcessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.camunda.process.test.api.CamundaAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@CamundaProcessTest
class ElementTemplateTest {

    private static final String TEST_PROCESS = "test.bpmn";
    private static final String PROCESS_ID = "CarbonReductorTestProcess";
    private static final String CARBON_REDUCTOR_ID = "Carbon_Reductor";
    private static final String CHECK_VARIABLES = "check_variables";
    private static final String JOB_TYPE = "de.envite.greenbpm.carbonreductorconnector.carbonreductortask:1";

    private CamundaClient camundaClient;

    @BeforeEach
    void deployProcess() {
        deployResources(TEST_PROCESS);
    }

    @Test
    void testElementTemplate() {
        ProcessInstanceEvent processInstanceEvent = startNewProcessInstance(PROCESS_ID);
        assertThat(processInstanceEvent)
                .isActive()
                .hasCompletedElement("Event_Start", 1)
                .hasActiveElements(CARBON_REDUCTOR_ID);
        ActivatedJob activatedJob = activateJob(JOB_TYPE);
        Map<String, Object> inputVariables = activatedJob.getVariablesAsMap();
        assertThat(inputVariables).containsKeys(
                "remainingProcessDuration",
                "maximumProcessDuration",
                "location",
                "milestone",
                "errorHandling",
                "measurementOnly",
                "thresholdEnabled",
                "thresholdValue");
        completeTaskForJob(activatedJob, Map.of(
                "originalCarbon", 100,
                "actualCarbon", 50,
                "savedCarbon", 50,
                "reducedCarbon", 50,
                "executionDelayed", true,
                "delayedBy", 100000));
        ActivatedJob checkVariables = activateJob(CHECK_VARIABLES);
        Map<String, Object> outputVariables = checkVariables.getVariablesAsMap();
        assertThat(outputVariables).containsKeys(
                "originalCarbon",
                "actualCarbon",
                "savedCarbon",
                "reducedCarbon",
                "executionDelayed",
                "delayedBy");
    }

    private ProcessInstanceEvent startNewProcessInstance(final String process) {
        return camundaClient.newCreateInstanceCommand()
                .bpmnProcessId(process)
                .latestVersion()
                .send()
                .join();
    }

    private ActivatedJob activateJob(final String jobtype) {
        ActivateJobsResponse response = camundaClient.newActivateJobsCommand()
                .jobType(jobtype)
                .maxJobsToActivate(1)
                .send()
                .join();
        return response.getJobs().get(0);
    }

    private CompleteJobResponse completeTaskForJob(final ActivatedJob activatedJob, final Map<String, Object> variables) {
        return camundaClient.newCompleteCommand(activatedJob)
                .variables(variables)
                .send()
                .join();
    }

    private void deployResources(final String... resources) {
        final DeployResourceCommandStep1 commandStep1 = camundaClient.newDeployResourceCommand();

        DeployResourceCommandStep1.DeployResourceCommandStep2 commandStep2 = null;
        for (final String process : resources) {
            if (commandStep2 == null) {
                commandStep2 = commandStep1.addResourceFromClasspath(process);
            } else {
                commandStep2 = commandStep2.addResourceFromClasspath(process);
            }
        }
        commandStep2.send().join();
    }
}
