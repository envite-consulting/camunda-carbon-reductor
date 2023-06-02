package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils.TestDataGenerator.createCarbonReductorOutput;
import static de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils.TestDataGenerator.createInputVariables;
import static org.assertj.core.api.Assertions.assertThat;

class CarbonReductorVariableMapperTest {

    private final CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Test
    void should_map_all_fields_to_domain() {
        CarbonReductorInputVariable inputVariables = createInputVariables();
        inputVariables.setMaximumProcessDuration("PT20M");
        inputVariables.setErrorHandling(ExceptionHandlingEnum.THROW_BPMN_ERROR.toString());

        CarbonReductorConfiguration result = classUnderTest.mapToDomain(inputVariables);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getLocation().getValue()).isEqualTo(inputVariables.getLocation());
        softAssertions.assertThat(result.getMilestone().getValue()).isEqualTo(inputVariables.getMilestone());
        softAssertions.assertThat(result.getRemainingProcessTimeshift().getValue().toString()).isEqualTo(inputVariables.getRemainingProcessDuration());
        softAssertions.assertThat(result.getMaximumProcessTimeshift().getValue().toString()).isEqualTo(inputVariables.getMaximumProcessDuration());
        softAssertions.assertThat(result.getTimeshiftWindow().getValue().toString()).isEqualTo(inputVariables.getTimeshiftWindow());
        softAssertions.assertThat(result.getExceptionHandling()).isEqualTo(ExceptionHandlingEnum.THROW_BPMN_ERROR);
        softAssertions.assertAll();
    }

    @Test
    void should_map_all_fields_from_domain() {
        CarbonReduction outputDDD = createCarbonReductorOutput();

        CarbonReductorOutputVariable result = classUnderTest.mapFromDomain(outputDDD);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.isExecutionDelayed()).isEqualTo(outputDDD.getDelay().isExecutionDelayed());
        softAssertions.assertThat(result.getDelayedBy()).isEqualTo(outputDDD.getDelay().getDelayedBy());
        softAssertions.assertThat(result.getActualCarbon()).isEqualTo(outputDDD.getActualCarbon().getValue());
        softAssertions.assertThat(result.getOriginalCarbon()).isEqualTo(outputDDD.getOriginalCarbon().getValue());
        softAssertions.assertThat(result.getSavedCarbon()).isEqualTo(outputDDD.getSavedCarbon().getValue());
        softAssertions.assertThat(result.getCarbonReduction()).isEqualTo(outputDDD.calculateReduction().getValue());
        softAssertions.assertAll();
    }
}