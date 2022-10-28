package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReduction;
import org.junit.jupiter.api.Test;

import static de.envite.greenbpm.carbonreductorconnector.TestDataGenerator.createCarbonReductorOutput;
import static de.envite.greenbpm.carbonreductorconnector.TestDataGenerator.createInputVariables;
import static org.assertj.core.api.Assertions.assertThat;

class CarbonReductorVariableMapperTest {

    private final CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Test
    void should_map_all_fields_to_domain() {
        CarbonReductorInputVariable inputVariables = createInputVariables();
        inputVariables.setMaximumProcessDuration("PT20M");

        CarbonReductorConfiguration result = classUnderTest.mapToDomain(inputVariables);

        assertThat(result.getLocation().getValue()).isEqualTo(inputVariables.getLocation());
        assertThat(result.getCarbonReductorMode().getValue()).isEqualTo(inputVariables.getCarbonReductorMode());
        assertThat(result.getMilestone().getValue()).isEqualTo(inputVariables.getMilestone());
        assertThat(result.getRemainingProcessDuration().getValue()).isEqualTo(inputVariables.getRemainingProcessDuration());
        assertThat(result.getMaximumProcessDuration().getValue()).isEqualTo(inputVariables.getMaximumProcessDuration());
        assertThat(result.getTimeshiftWindow().getValue()).isEqualTo(inputVariables.getTimeshiftWindow());
    }

    @Test
    void should_map_all_fields_from_domain() {
        CarbonReduction outputDDD = createCarbonReductorOutput();

        CarbonReductorOutputVariable result = classUnderTest.mapFromDomain(outputDDD);

        assertThat(result.isExecutionDelayed()).isEqualTo(outputDDD.getDelay().isExecutionDelayed());
        assertThat(result.getDelayedBy()).isEqualTo(outputDDD.getDelay().getDelayedBy());
        assertThat(result.getActualCarbon()).isEqualTo(outputDDD.getActualCarbon().getValue());
        assertThat(result.getOriginalCarbon()).isEqualTo(outputDDD.getOriginalCarbon().getValue());
        assertThat(result.getSavedCarbon()).isEqualTo(outputDDD.getSavedCarbon().getValue());
        assertThat(result.getCarbonReduction()).isEqualTo(outputDDD.calculateReduction().getValue());
    }
}