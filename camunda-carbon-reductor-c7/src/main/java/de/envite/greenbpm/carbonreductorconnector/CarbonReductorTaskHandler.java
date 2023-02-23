package de.envite.greenbpm.carbonreductorconnector;


import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@ExternalTaskSubscription(topicName = "CarbonReductor")
public class CarbonReductorTaskHandler implements ExternalTaskHandler {

    static final int RETRIES_MAGIC_NUMBER = 999;

    private final DelayCalculator delayCalculator;
    private final CarbonReductorVariableMapper carbonReductorVariableMapper;
    private final PPMService ppmService;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        log.debug("Entering Carbon Reductor with variables: {}", externalTask.getAllVariables());

        if (maxRetriesReached(externalTask)) {
            log.info("Completing previously time shifted job {}", externalTask.getProcessInstanceId());
            externalTaskService.complete(externalTask);
            return;
        }

        Map<String, Object> variables = externalTask.getAllVariables();
        try {
            variables.put("milestone", ppmService.getRemainingDurationPrediction());
        } catch (Exception e){
            log.error("Could not predict remain time via PPM", e);
        }
        CarbonReductorConfiguration carbonReductorConfiguration = carbonReductorVariableMapper.mapToDomain(variables);

        try {
            CarbonReduction carbonReductorOutput = delayCalculator.calculateDelay(carbonReductorConfiguration);
            delayOrExecute(externalTask, externalTaskService, carbonReductorOutput);
        } catch (CarbonReductorException e) {
            throw new RuntimeException(e);
        }
    }

    private void delayOrExecute(ExternalTask externalTask, ExternalTaskService externalTaskService, CarbonReduction carbonReductorOutput) {
        Map<String, Object> outputVariables = carbonReductorVariableMapper.mapFromDomain(carbonReductorOutput, externalTask.getAllVariables());

        if (carbonReductorOutput.getDelay().isExecutionDelayed()) {
            Duration duration = Duration.ofMillis(carbonReductorOutput.getDelay().getDelayedBy());
            log.info("Time shifting job {} by {}", externalTask.getProcessInstanceId(), duration);
            failJobWithRetry(externalTaskService, externalTask, duration, outputVariables);
        } else {
            log.info("Executing job {} immediately", externalTask.getProcessInstanceId());
            externalTaskService.complete(externalTask, outputVariables);
        }
    }

    private void failJobWithRetry(ExternalTaskService externalTaskService, ExternalTask externalTask,
                                  Duration duration, Map<String, Object> variableMap) {
        externalTaskService.handleFailure(
                externalTask.getId(),
                "Time shifting execution",
                "Time shifting execution due for a lower carbon footprint",
                RETRIES_MAGIC_NUMBER,
                duration.toMillis(),
                variableMap,
                null);
    }

    private boolean maxRetriesReached(ExternalTask externalTask) {
        return externalTask.getRetries() != null && externalTask.getRetries() == RETRIES_MAGIC_NUMBER;
    }
}
