package de.envite.greenbpm.carbonreductorconnector

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction
import de.envite.greenbpm.carbonreductor.core.domain.service.CarbonReductorException
import de.envite.greenbpm.carbonreductor.core.usecase.`in`.DelayCalculator
import de.envite.greenbpm.carbonreductorconnector.utils.lazyLogger
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription
import org.camunda.bpm.client.task.ExternalTask
import org.camunda.bpm.client.task.ExternalTaskHandler
import org.camunda.bpm.client.task.ExternalTaskService
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Duration.ofMillis

@Component
@Slf4j
@RequiredArgsConstructor
@ExternalTaskSubscription(topicName = "CarbonReductor")
class CarbonReductorTaskHandler(
    private val delayCalculator: DelayCalculator,
) : ExternalTaskHandler {

    val log by lazyLogger()
    internal val retriesMagicNumber = 999
    internal val errorMessage = "Time shifting execution"
    internal val errorDetails = "Time shifting execution due for a lower carbon footprint"

    override fun execute(externalTask: ExternalTask, externalTaskService: ExternalTaskService) {
        log.debug("Entering Carbon Reductor with variables: {}", externalTask.allVariables)
        if (maxRetriesReached(externalTask)) {
            log.info(
                "Completing previously time shifted job {}",
                externalTask.processInstanceId
            )
            return externalTaskService.complete(externalTask)
        }
        val carbonReductorConfiguration = mapToDomain(externalTask.allVariables)
        try {
            val carbonReductorOutput = delayCalculator.calculateDelay(carbonReductorConfiguration)
            delayOrExecute(externalTask, externalTaskService, carbonReductorOutput)
        } catch (e: CarbonReductorException) {
            throw RuntimeException(e)
        }
    }

    private fun delayOrExecute(
        externalTask: ExternalTask,
        externalTaskService: ExternalTaskService,
        carbonReductorOutput: CarbonReduction
    ) {
        val outputVariables = mapFromDomain(carbonReductorOutput, externalTask.allVariables)
        if (carbonReductorOutput.delay.isExecutionDelayed) {
            val duration = ofMillis(carbonReductorOutput.delay.delayedBy)
            log.info("Time shifting job {} by {}", externalTask.processInstanceId, duration)
            failJobWithRetry(externalTaskService, externalTask, duration, outputVariables)
        } else {
            log.info("Executing job {} immediately", externalTask.processInstanceId)
            externalTaskService.complete(externalTask, outputVariables)
        }
    }

    private fun failJobWithRetry(
        externalTaskService: ExternalTaskService, externalTask: ExternalTask,
        duration: Duration, variableMap: Map<String, Any>
    ) {
        externalTaskService.handleFailure(
            externalTask.id,
            errorMessage,
            errorDetails,
            retriesMagicNumber,
            duration.toMillis(),
            variableMap,
            null
        )
    }

    private fun maxRetriesReached(externalTask: ExternalTask): Boolean {
        return externalTask.retries != null && externalTask.retries == retriesMagicNumber
    }
}