package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@ZeebeProcessTest
class ElementTemplateTest {

    private static final String TEST_PROCESS = "test.bpmn";
    private static final String PROCESS_ID = "CarbonReductorTestProcess";
    private static final String CARBON_REDUCTOR_ID = "Carbon_Reductor";
    private static final String CHECK_VARIABLES = "check_variables";
    private static final String JOB_TYPE = "de.envite.greenbpm.carbonreductorconnector.carbonreductortask:1";

    private ZeebeTestEngine engine;
    private ZeebeClient zeebeClient;

    @BeforeEach
    void deployProcess() {
        DeploymentEvent deploymentEvent = deployResources(TEST_PROCESS);
        assertThat(deploymentEvent).containsProcessesByResourceName(TEST_PROCESS);
    }

    @Test
    void testElementTemplate() throws InterruptedException, TimeoutException {
        ProcessInstanceEvent processInstanceEvent = startNewProcessInstance(PROCESS_ID);
        assertThat(processInstanceEvent)
                .isActive()
                .hasPassedElement("Event_Start")
                .isWaitingAtElements(CARBON_REDUCTOR_ID);
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
        CompleteJobResponse completeJobResponse = completeTaskForJob(activatedJob, Map.of(
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

    private ProcessInstanceEvent startNewProcessInstance(final String process) throws InterruptedException, TimeoutException {
        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(process)
                .latestVersion()
                .send()
                .join();
        waitForIdleState(Duration.ofSeconds(1));
        return processInstanceEvent;
    }

    private ActivatedJob activateJob(final String jobtype) throws InterruptedException, TimeoutException {
        ActivateJobsResponse response = zeebeClient.newActivateJobsCommand()
                .jobType(jobtype)
                .maxJobsToActivate(1)
                .send()
                .join();
        waitForIdleState(Duration.ofSeconds(1));
        return response.getJobs().get(0);
    }

    private CompleteJobResponse completeTaskForJob(final ActivatedJob activatedJob, final Map<String, Object> variables) throws InterruptedException, TimeoutException {
        CompleteJobResponse completeJobResponse = zeebeClient.newCompleteCommand(activatedJob)
                .variables(variables)
                .send()
                .join();
        waitForIdleState(Duration.ofSeconds(1));
        return completeJobResponse;
    }

    private DeploymentEvent deployResources(final String... resources) {
        final DeployResourceCommandStep1 commandStep1 = zeebeClient.newDeployResourceCommand();

        DeployResourceCommandStep1.DeployResourceCommandStep2 commandStep2 = null;
        for (final String process : resources) {
            if (commandStep2 == null) {
                commandStep2 = commandStep1.addResourceFromClasspath(process);
            } else {
                commandStep2 = commandStep2.addResourceFromClasspath(process);
            }
        }

        return commandStep2.send().join();
    }

    private void waitForIdleState(final Duration duration) throws InterruptedException, TimeoutException {
        engine.waitForIdleState(duration);
    }
}
