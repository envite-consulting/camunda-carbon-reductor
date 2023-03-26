package de.envite.greenbpm.carbonreductorconnector

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay
import io.github.domainprimitives.validation.InvariantException
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MappingKtTest {
    @Nested
    internal inner class ToDomain {
        @Test
        fun should_map_all_fields_from_map() {
            val variables: MutableMap<String, Any> = HashMap()
            variables["location"] = "here"
            variables["carbonReductorMode"] = "test"
            variables["milestone"] = DateTime.parse("2023-02-10T15:48:10.285+01:00")
            variables["maximumProcessDuration"] = "PT10M"
            variables["remainingProcessDuration"] = "PT6H"
            val result: CarbonReductorConfiguration = mapToDomain(variables)
            val softAssertions = SoftAssertions()
            softAssertions.assertThat(result.location.value).isEqualTo(variables["location"])
            softAssertions.assertThat(result.carbonReductorMode.value).isEqualTo(variables["carbonReductorMode"])
            softAssertions.assertThat(result.milestone.value).isEqualTo("2023-02-10T14:48:10.285Z[Etc/UTC]")
            softAssertions.assertThat(result.maximumProcessTimeshift.value.toString())
                .isEqualTo(variables["maximumProcessDuration"])
            softAssertions.assertThat(result.remainingProcessTimeshift.value.toString())
                .isEqualTo(variables["remainingProcessDuration"])
            softAssertions.assertThat(result.timeshiftWindow).isNull()
            softAssertions.assertAll()
        }

        @Test
        fun should_map_optional_null_fields() {
            val variables: MutableMap<String, Any> = HashMap()
            variables["location"] = "here"
            variables["carbonReductorMode"] = "test"
            variables["milestone"] = DateTime.parse("2023-02-10T15:48:10.285+01:00")
            Assertions.assertThatThrownBy { mapToDomain(variables) }.isInstanceOf(
                InvariantException::class.java
            )
        }
    }

    @Nested
    internal inner class FromDomain {
        @Test
        fun should_map_all_fields_to_map() {
            val carbonReduction = CarbonReduction(
                Delay(true, 3),
                Carbon(1.0),
                Carbon(2.0),
                Carbon(3.0)
            )
            val variables = java.util.Map.of<String, Any>("milestone", DateTime.parse("2023-02-10T15:48:10.285+01:00"))
            val result: Map<String, Any> = mapFromDomain(carbonReduction, variables)
            val softAssertions = SoftAssertions()
            softAssertions.assertThat(result["executionDelayed"]).isEqualTo(carbonReduction.delay.isExecutionDelayed)
            softAssertions.assertThat(result["originalCarbon"]).isEqualTo(carbonReduction.originalCarbon.value)
            softAssertions.assertThat(result["actualCarbon"]).isEqualTo(carbonReduction.actualCarbon.value)
            softAssertions.assertThat(result["savedCarbon"]).isEqualTo(carbonReduction.savedCarbon.value)
            softAssertions.assertThat(result["reducedCarbon"]).isEqualTo(carbonReduction.calculateReduction().value)
            softAssertions.assertThat(result["delayedBy"]).isEqualTo(carbonReduction.delay.delayedBy)
            softAssertions.assertThat(result["milestone"]).isEqualTo("2023-02-10T14:48:10.285Z[Etc/UTC]")
            softAssertions.assertAll()
        }
    }
}