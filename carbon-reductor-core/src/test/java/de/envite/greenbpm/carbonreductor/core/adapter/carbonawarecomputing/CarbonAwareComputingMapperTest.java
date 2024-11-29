package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarbonAwareComputingMapperTest {

    private final CarbonAwareComputingMapper classUnderTest = new CarbonAwareComputingMapper();

    @Test
    void should_map_all_fields_for_timestamp_in_the_past() {
        EmissionsData emissionsData = mock(EmissionsData.class);
        when(emissionsData.getValue()).thenReturn(2.0);
        when(emissionsData.getTimestamp()).thenReturn(OffsetDateTime.MIN);

        EmissionTimeframe result = classUnderTest.mapToDomain(emissionsData);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getOptimalTime().getValue()).isEqualTo(emissionsData.getTimestamp());
        softAssertions.assertThat(result.getEarliestForecastedValue().getValue()).isEqualTo(emissionsData.getValue());
        softAssertions.assertThat(result.getOptimalValue().getValue()).isEqualTo(emissionsData.getValue());
        softAssertions.assertAll();
    }

    @Test
    void should_map_all_fields_except_earliestForecastedValue_for_timestamp_in_the_future() {
        EmissionsData emissionsData = mock(EmissionsData.class);
        when(emissionsData.getValue()).thenReturn(2.0);
        when(emissionsData.getTimestamp()).thenReturn(OffsetDateTime.now().plusHours(12));

        EmissionTimeframe result = classUnderTest.mapToDomain(emissionsData);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getOptimalTime().getValue()).isEqualTo(emissionsData.getTimestamp());
        softAssertions.assertThat(result.getEarliestForecastedValue().getValue()).isEqualTo(0.0);
        softAssertions.assertThat(result.getOptimalValue().getValue()).isEqualTo(emissionsData.getValue());
        softAssertions.assertAll();
    }
}