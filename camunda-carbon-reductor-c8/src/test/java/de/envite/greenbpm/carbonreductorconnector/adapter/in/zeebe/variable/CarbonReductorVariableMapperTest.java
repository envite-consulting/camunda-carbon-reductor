package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;

import static de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.test.utils.TestDataGenerator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarbonReductorVariableMapperTest {

    private final CarbonReductorVariableMapper classUnderTest = new CarbonReductorVariableMapper();

    @Nested
    class MapToDomain {

        @Test
        void should_map_all_fields() {
            CarbonReductorInputVariable inputVariables = createInputVariables();
            inputVariables.setMaximumProcessDuration("PT20M");
            inputVariables.setErrorHandling(ExceptionHandlingEnum.THROW_BPMN_ERROR.toString());
            inputVariables.setMeasurementOnly(true);

            CarbonReductorConfiguration result = classUnderTest.mapToDomain(inputVariables);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.getLocation().getValue()).isEqualTo(inputVariables.getLocation());
            softAssertions.assertThat(result.getMilestone().getValue()).isEqualTo(OffsetDateTime.of(
                    LocalDate.of(2020, 7, 31),
                    LocalTime.of(14, 27, 30, 766000000),
                    ZoneId.of("UTC").getRules().getOffset(Instant.now())
            ));
            softAssertions.assertThat(result.getRemainingProcessDuration().getValue().toString()).isEqualTo(inputVariables.getRemainingProcessDuration());
            softAssertions.assertThat(result.getMaximumProcessDuration().getValue().toString()).isEqualTo(inputVariables.getMaximumProcessDuration());
            softAssertions.assertThat(result.getExceptionHandling()).isEqualTo(ExceptionHandlingEnum.THROW_BPMN_ERROR);
            softAssertions.assertThat(result.isMeasurementOnly()).isTrue();
            softAssertions.assertAll();
        }

        @Test
        void should_throw_on_invalid_date() {
            CarbonReductorInputVariable inputVariables = createInputVariables();
            inputVariables.setMilestone("2020-07-31T14:27:30@Europe/Berlin");

            assertThatThrownBy(() -> classUnderTest.mapToDomain(inputVariables))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith("Milestone: Unknown date time format (Expected yyyy-MM-ddTHH:mm:ss.SSSX[z])");
        }
    }

    @Nested
    class MapFromDomain {

        @Test
        void should_map_all_fields_from_domain() {
            CarbonReduction outputDDD = createCarbonReductorOutput();

            CarbonReductorOutputVariable result = classUnderTest.mapFromDomain(outputDDD);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.isExecutionDelayed()).isEqualTo(outputDDD.getDelay().isExecutionDelayed());
            softAssertions.assertThat(result.getDelayedBy()).isEqualTo(outputDDD.getDelay().getDelayedBy());
            softAssertions.assertThat(result.getOptimalForecastedCarbon()).isEqualTo(outputDDD.getOptimalForecastedCarbon().getValue());
            softAssertions.assertThat(result.getCarbonWithoutOptimization()).isEqualTo(outputDDD.getCarbonWithoutOptimization().getValue());
            softAssertions.assertThat(result.getSavedCarbonPercentage()).isEqualTo(outputDDD.getSavedCarbonPercentage().getValue());
            softAssertions.assertThat(result.getCarbonReduction()).isEqualTo(outputDDD.calculateReduction().getValue());
            softAssertions.assertAll();
        }

        @Test
        void should_map_mandatory_fields_from_domain() {
            CarbonReduction outputDDD = createCarbonReductorOutputWithoutCurrentValue();

            CarbonReductorOutputVariable result = classUnderTest.mapFromDomain(outputDDD);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.isExecutionDelayed()).isEqualTo(outputDDD.getDelay().isExecutionDelayed());
            softAssertions.assertThat(result.getDelayedBy()).isEqualTo(outputDDD.getDelay().getDelayedBy());
            softAssertions.assertThat(result.getOptimalForecastedCarbon()).isEqualTo(outputDDD.getOptimalForecastedCarbon().getValue());
            softAssertions.assertThat(result.getCarbonWithoutOptimization()).isNull();
            softAssertions.assertThat(result.getSavedCarbonPercentage()).isNull();
            softAssertions.assertThat(result.getCarbonReduction()).isNull();
            softAssertions.assertAll();
        }
    }
}