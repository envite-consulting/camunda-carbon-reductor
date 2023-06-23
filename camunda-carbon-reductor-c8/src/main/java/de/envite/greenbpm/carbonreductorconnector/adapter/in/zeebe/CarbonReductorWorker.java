package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorInputVariable;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorOutputVariable;
import de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable.CarbonReductorVariableMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarbonReductorWorker {

    private final ZeebeClient client;
    private final DelayCalculator delayCalculator;

    private final CarbonReductorVariableMapper variableMapper;

    private static final int RETRIES_MAGIC_VALUE = 999;

    @JobWorker(type = "de.envite.greenbpm.carbonreductorconnector.carbonreductortask:1", autoComplete = false)
    public void execute(ActivatedJob job) {
        if (!timeShiftMustBeDetermined(job)) {
            log.info("Completing previously time shifted job {}", job.getProcessInstanceKey());
            completeJob(job);
            return;
        }

        CarbonReductorConfiguration carbonReductorConfiguration = getCarbonReductorInput(job);
        CarbonReduction carbonReductorOutput;
        try {
            carbonReductorOutput = delayCalculator.calculateDelay(carbonReductorConfiguration);
        } catch (CarbonReductorException e) {
            CarbonReduction defaultCarbonReductorOutput = new CarbonReduction(new Delay(false, 0L), new Carbon(0.0), new Carbon(0.0), new Percentage(0.0));
            writeOutputToProcessInstance(job, defaultCarbonReductorOutput);
            client.newThrowErrorCommand(job)
                    .errorCode("carbon-reductor-error")
                    .errorMessage(e.getMessage())
                    .send();
            return;
        }

        writeOutputToProcessInstance(job, carbonReductorOutput);

        if (carbonReductorConfiguration.isMeasurementOnly()) {
            log.info("Executing job {} immediately for measurement only", job.getProcessInstanceKey());
            completeJob(job);
            return;
        }

        if (carbonReductorOutput.getDelay().isExecutionDelayed()) {
            Duration duration = Duration.ofMillis(carbonReductorOutput.getDelay().getDelayedBy());
            log.info("Time shifting job {} by {}", job.getProcessInstanceKey(), duration);
            failJobWithRetry(job, duration);
        } else {
            log.info("Executing job {} immediately", job.getProcessInstanceKey());
            completeJob(job);
        }
    }

    private CarbonReductorConfiguration getCarbonReductorInput(ActivatedJob job) {
        CarbonReductorInputVariable inputVariables = job.getVariablesAsType(CarbonReductorInputVariable.class);
        log.debug("input: {}", inputVariables);
        return variableMapper.mapToDomain(inputVariables);
    }

    private void completeJob(ActivatedJob job) {
        client.newCompleteCommand(job)
                .send()
                .exceptionally( throwable -> { throw new RuntimeException("Could not complete job %s".formatted(job), throwable); });
    }

    private void failJobWithRetry(ActivatedJob job, Duration duration) {
        client.newFailCommand(job)
                .retries(RETRIES_MAGIC_VALUE)
                .retryBackoff(duration)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not fail job %s".formatted(job), throwable);
                });
    }

    private void writeOutputToProcessInstance(ActivatedJob job, CarbonReduction output) {
        CarbonReductorOutputVariable outputVariable = variableMapper.mapFromDomain(output);
        client.newSetVariablesCommand(job.getElementInstanceKey())
                .variables(outputVariable)
                .send();
    }

    private boolean timeShiftMustBeDetermined(ActivatedJob job) {
        return job.getRetries() != RETRIES_MAGIC_VALUE;
    }
}
