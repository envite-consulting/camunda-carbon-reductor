package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.usecase.in.DelayCalculator;
import io.camunda.connector.api.validation.ValidationProvider;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ServiceLoader;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarbonReductorWorker {

    private final ZeebeClient client;
    private final DelayCalculator delayCalculator;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final int RETRIES_MAGIC_VALUE = 999;

    @JobWorker(type = "de.envite.greenbpm.carbonreductorconnector.carbonreductortask:1", autoComplete = false)
    public void execute(ActivatedJob job) throws Exception {
        if (timeShiftMustBeDetermined(job)) {
            CarbonReductorInput carbonReductorInput = getCarbonReductorInput(job);
            CarbonReductorOutput carbonReductorOutput = delayCalculator.calculateDelay(carbonReductorInput);

            writeOutputToProcessInstance(job, carbonReductorOutput);

            if (carbonReductorOutput.isExecutionDelayed()) {
                Duration duration = Duration.ofMillis(carbonReductorOutput.getDelayedBy());
                log.info("Time shifting job {} by {}", job.getProcessInstanceKey(), duration);
                failJobWithRetry(job, duration);
            } else {
                log.info("Executing job {} immediately", job.getProcessInstanceKey());
                completeJob(job);
            }
        } else {
            log.info("Completing previously time shifted job {}", job.getProcessInstanceKey());
            completeJob(job);
        }
    }

    private CarbonReductorInput getCarbonReductorInput(ActivatedJob job) throws JsonProcessingException {
        CarbonReductorInput carbonReductorInput = objectMapper.readValue(job.getVariables(), CarbonReductorInput.class);
        getValidationProvider().validate(carbonReductorInput);
        log.debug("input: {}", carbonReductorInput);
        return carbonReductorInput;
    }

    private void completeJob(ActivatedJob job) {
        client.newCompleteCommand(job)
                .send()
                .exceptionally( throwable -> { throw new RuntimeException(String.format("Could not complete job %s", job), throwable); });
    }

    private void failJobWithRetry(ActivatedJob job, Duration duration) {
        client.newFailCommand(job)
                .retries(RETRIES_MAGIC_VALUE)
                .retryBackoff(duration)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException(String.format("Could not fail job %s", job), throwable);
                });
    }

    private void writeOutputToProcessInstance(ActivatedJob job, CarbonReductorOutput output) {
        client.newSetVariablesCommand(job.getElementInstanceKey())
                .variables(output)
                .send();
    }

    private boolean timeShiftMustBeDetermined(ActivatedJob job) {
        return job.getRetries() != RETRIES_MAGIC_VALUE;
    }

    private ValidationProvider getValidationProvider () {
        return ServiceLoader.load(ValidationProvider.class)
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Please bind an implementation to "
                                                + ValidationProvider.class.getName()
                                                + " via SPI"));
    }
}
