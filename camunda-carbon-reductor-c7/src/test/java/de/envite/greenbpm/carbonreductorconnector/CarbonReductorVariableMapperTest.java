package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class CarbonReductorVariableMapperTest {

    private CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Test
    void should_map_all_fields_from_map() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("location", "here");
        variables.put("carbonReductorMode", "test");
        variables.put("milestone", "2022-10-20T11:35:45.826Z[Etc/UTC]");
        variables.put("maximumProcessDuration", "PT10M");

        CarbonReductorConfiguration result = classUnderTest.mapToDomain(variables);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getLocation().getValue()).isEqualTo(variables.get("location"));
        softAssertions.assertThat(result.getCarbonReductorMode().getValue()).isEqualTo(variables.get("carbonReductorMode"));
        softAssertions.assertThat(result.getMilestone().getValue()).isEqualTo(variables.get("milestone"));
        softAssertions.assertThat(result.getMaximumProcessTimeshift().getValue()).isEqualTo(variables.get("maximumProcessDuration"));
        softAssertions.assertThat(result.getRemainingProcessTimeshift()).isNull();
        softAssertions.assertThat(result.getTimeshiftWindow()).isNull();
        softAssertions.assertAll();
    }

    @Test
    void should_map_all_fields_to_map() {
        CarbonReduction carbonReduction = new CarbonReduction(
                new Delay(true, 3),
                new Carbon(1.0),
                new Carbon(2.0),
                new Carbon(3.0)
        );

        Map<String, Object> result = classUnderTest.mapFromDomain(carbonReduction);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.get("executionDelayed")).isEqualTo(carbonReduction.getDelay().isExecutionDelayed());
        softAssertions.assertThat(result.get("originalCarbon")).isEqualTo(carbonReduction.getOriginalCarbon().getValue());
        softAssertions.assertThat(result.get("actualCarbon")).isEqualTo(carbonReduction.getActualCarbon().getValue());
        softAssertions.assertThat(result.get("savedCarbon")).isEqualTo(carbonReduction.getSavedCarbon().getValue());
        softAssertions.assertThat(result.get("carbonReduction")).isEqualTo(carbonReduction.calculateReduction().getValue());
        softAssertions.assertThat(result.get("delayedBy")).isEqualTo(carbonReduction.getDelay().getDelayedBy());
        softAssertions.assertAll();
    }
}