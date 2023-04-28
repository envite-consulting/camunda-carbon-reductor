package de.envite.greenbpm.carbonreductor.core.adapter.watttime;

import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsDataDTO;
import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import io.github.domainprimitives.validation.InvariantException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonAwareApiMapperTest {

    private final CarbonAwareApiMapper classUnderTest = new CarbonAwareApiMapper();

    @Test
    void should_map_to_domain() {
        final String optimalTime = "2007-12-03T10:15:30+01:00";
        EmissionsForecastDTO emissionsForecastDTO = new EmissionsForecastDTO();
        EmissionsDataDTO optimalDataPoint = new EmissionsDataDTO();
        optimalDataPoint.setTimestamp(OffsetDateTime.parse(optimalTime));
        optimalDataPoint.setValue(2.0);
        emissionsForecastDTO.setOptimalDataPoints(List.of(optimalDataPoint, new EmissionsDataDTO()));

        EmissionsDataDTO currentEmission = new EmissionsDataDTO();
        currentEmission.setValue(40.0);
        emissionsForecastDTO.setForecastData(List.of(currentEmission, new EmissionsDataDTO()));

        EmissionTimeframe result = classUnderTest.mapToDomain(emissionsForecastDTO);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getOptimalTime().getValue()).isEqualTo(OffsetDateTime.parse(optimalTime));
        softAssertions.assertThat(result.getRating().getValue()).isEqualTo(40.0);
        softAssertions.assertThat(result.getForecastedValue().getValue()).isEqualTo(2.0);
        softAssertions.assertAll();
    }

    @Test
    void should_throw_if_no_optimal_data_points() {
        assertThatThrownBy(() -> classUnderTest.mapToDomain(new EmissionsForecastDTO()))
                .isExactlyInstanceOf(InvariantException.class)
                .hasMessageContaining("Optimal Time should not be null");
    }

    @Test
    void should_throw_if_no_current_emissions() {
        EmissionsForecastDTO emissionsForecastDTO = new EmissionsForecastDTO();
        EmissionsDataDTO optimalDataPoint = new EmissionsDataDTO();
        optimalDataPoint.setTimestamp(OffsetDateTime.now());
        optimalDataPoint.setValue(2.0);
        emissionsForecastDTO.setOptimalDataPoints(List.of(optimalDataPoint, new EmissionsDataDTO()));

        assertThatThrownBy(() -> classUnderTest.mapToDomain(emissionsForecastDTO))
                .isExactlyInstanceOf(InvariantException.class)
                .hasMessageContaining("Rating should not be null");
    }
}