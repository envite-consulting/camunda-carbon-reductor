package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import org.junit.jupiter.api.Test;

import static de.envite.greenbpm.carbonreductorconnector.TestDataGenerator.createInputVariables;
import static org.assertj.core.api.Assertions.assertThat;

class CarbonReductorVariableMapperTest {

    private CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Test
    public void should_map_all_fields_to_domain() {
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
}