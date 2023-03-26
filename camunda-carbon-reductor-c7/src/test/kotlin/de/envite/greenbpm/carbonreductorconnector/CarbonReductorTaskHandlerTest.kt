package de.envite.greenbpm.carbonreductorconnector

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration
import de.envite.greenbpm.carbonreductor.core.usecase.`in`.DelayCalculator
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.camunda.bpm.client.task.ExternalTask
import org.camunda.bpm.client.task.ExternalTaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
class CarbonReductorTaskHandlerTest {

    private lateinit var classUnderTest: CarbonReductorTaskHandler

    @MockK
    private lateinit var delayCalculatorMock: DelayCalculator

    private val externalTask = mockk<ExternalTask>()

    @BeforeEach
    fun setUp() {
        mockkStatic(::mapToDomain)
        mockkStatic(::mapFromDomain)
        every { externalTask.processInstanceId }.returns("1")
        every { externalTask.id }.returns("11")

        classUnderTest = CarbonReductorTaskHandler(delayCalculatorMock)
    }

    @Test
    fun should_query_reduction_write_variables_and_set_retry_if_delay() {
        every { externalTask.retries }.returns(classUnderTest.retriesMagicNumber - 1)
        val externalTaskService = mockk<ExternalTaskService>()
        val carbonReductorConfiguration = mockk<CarbonReductorConfiguration>()
        every { mapToDomain(externalTask.allVariables) }.returns(carbonReductorConfiguration)
        val duration = Duration.parse("PT10S")
        val carbonReductorOutput = mockk<CarbonReduction>()
        every { carbonReductorOutput.delay.isExecutionDelayed }.returns(true)
        every { carbonReductorOutput.delay.delayedBy }.returns(duration.toMillis())
        every { delayCalculatorMock.calculateDelay(carbonReductorConfiguration) }.returns(carbonReductorOutput)
        val outputVariables = mapOf<String, Any>()
        every { mapFromDomain(carbonReductorOutput, externalTask.allVariables) }.returns(outputVariables)
        every {
            externalTaskService.handleFailure(any(), any(), any(), any(), any(), any(), any())
        } just Runs

        classUnderTest.execute(externalTask, externalTaskService)
    }

    @Test
    fun should_query_reduction_write_variables_and_complete_if_no_delay() {
        every { externalTask.retries }.returns(classUnderTest.retriesMagicNumber - 1)
        val externalTaskService = mockk<ExternalTaskService>()
        val carbonReductorConfiguration = mockk<CarbonReductorConfiguration>()
        every { mapToDomain(externalTask.allVariables) }.returns(carbonReductorConfiguration)
        val carbonReductorOutput = mockk<CarbonReduction>()
        every { carbonReductorOutput.delay.isExecutionDelayed }.returns(false)
        every { delayCalculatorMock.calculateDelay(carbonReductorConfiguration) }.returns(carbonReductorOutput)
        val outputVariables = mapOf<String, Any>()
        every { mapFromDomain(carbonReductorOutput, externalTask.allVariables) }.returns(outputVariables)
        every { externalTaskService.complete(externalTask, outputVariables) } just runs

        classUnderTest.execute(externalTask, externalTaskService)
    }

    @Test
    fun should_complete_if_max_limit_is_reached() {
        every { externalTask.retries }.returns(classUnderTest.retriesMagicNumber)
        every { externalTask.allVariables }.returns(mapOf())
        val externalTaskService = mockk<ExternalTaskService>()
        every { externalTaskService.complete(externalTask) } just runs

        classUnderTest.execute(externalTask, externalTaskService)

        verify { delayCalculatorMock wasNot called }
    }
}