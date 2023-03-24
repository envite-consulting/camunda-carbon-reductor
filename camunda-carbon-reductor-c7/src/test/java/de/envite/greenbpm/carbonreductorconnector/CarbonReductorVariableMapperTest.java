package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import io.github.domainprimitives.validation.InvariantException;
import org.assertj.core.api.SoftAssertions;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonReductorVariableMapperTest {

    private CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Nested
    class ToDomain {
        @Test
        void should_map_all_fields_from_map() {
            Map<String, Object> variables = new HashMap<>();
            variables.put("location", "here");
            variables.put("carbonReductorMode", "test");
            variables.put("milestone", DateTime.parse("2023-02-10T15:48:10.285+01:00"));
            variables.put("maximumProcessDuration", "PT10M");
            variables.put("remainingProcessDuration", "PT6H");

            CarbonReductorConfiguration result = classUnderTest.mapToDomain(variables);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.getLocation().getValue()).isEqualTo(variables.get("location"));
            softAssertions.assertThat(result.getCarbonReductorMode().getValue()).isEqualTo(variables.get("carbonReductorMode"));
            softAssertions.assertThat(result.getMilestone().getValue()).isEqualTo("2023-02-10T14:48:10.285Z[Etc/UTC]");
            softAssertions.assertThat(result.getMaximumProcessTimeshift().getValue().toString()).isEqualTo(variables.get("maximumProcessDuration"));
            softAssertions.assertThat(result.getRemainingProcessTimeshift().getValue().toString()).isEqualTo(variables.get("remainingProcessDuration"));
            softAssertions.assertThat(result.getTimeshiftWindow()).isNull();
            softAssertions.assertAll();
        }

        @Test
        void should_map_optional_null_fields() {
            Map<String, Object> variables = new HashMap<>();
            variables.put("location", "here");
            variables.put("carbonReductorMode", "test");
            variables.put("milestone", DateTime.parse("2023-02-10T15:48:10.285+01:00"));

            assertThatThrownBy(() -> classUnderTest.mapToDomain(variables)).isInstanceOf(InvariantException.class);
        }
    }

    @Nested
    class FromDomain {
        @Test
        void should_map_all_fields_to_map() {
            CarbonReduction carbonReduction = new CarbonReduction(
                    new Delay(true, 3),
                    new Carbon(1.0),
                    new Carbon(2.0),
                    new Carbon(3.0)
            );
            Map<String, Object> variables = Map.of("milestone", DateTime.parse("2023-02-10T15:48:10.285+01:00"));

            Map<String, Object> result = classUnderTest.mapFromDomain(carbonReduction, variables);
            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.get("executionDelayed")).isEqualTo(carbonReduction.getDelay().isExecutionDelayed());
            softAssertions.assertThat(result.get("originalCarbon")).isEqualTo(carbonReduction.getOriginalCarbon().getValue());
            softAssertions.assertThat(result.get("actualCarbon")).isEqualTo(carbonReduction.getActualCarbon().getValue());
            softAssertions.assertThat(result.get("savedCarbon")).isEqualTo(carbonReduction.getSavedCarbon().getValue());
            softAssertions.assertThat(result.get("reducedCarbon")).isEqualTo(carbonReduction.calculateReduction().getValue());
            softAssertions.assertThat(result.get("delayedBy")).isEqualTo(carbonReduction.getDelay().getDelayedBy());
            softAssertions.assertThat(result.get("milestone")).isEqualTo("2023-02-10T14:48:10.285Z[Etc/UTC]");
            softAssertions.assertAll();
        }
    }
}