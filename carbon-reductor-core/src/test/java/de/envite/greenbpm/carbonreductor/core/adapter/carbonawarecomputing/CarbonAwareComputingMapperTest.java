package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import io.github.domainprimitives.validation.InvariantException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        when(emissionsData.getTimestamp()).thenReturn(OffsetDateTime.now(ZoneOffset.UTC).plusHours(12));

        EmissionTimeframe result = classUnderTest.mapToDomain(emissionsData);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.getOptimalTime().getValue()).isEqualTo(emissionsData.getTimestamp());
        softAssertions.assertThat(result.getEarliestForecastedValue().getValue()).isEqualTo(0.0);
        softAssertions.assertThat(result.getOptimalValue().getValue()).isEqualTo(emissionsData.getValue());
        softAssertions.assertAll();
    }

    @Test
    void should_throw_if_timestamp_is_null() {
        EmissionsData emissionsData = mock(EmissionsData.class);
        when(emissionsData.getValue()).thenReturn(2.0);
        when(emissionsData.getTimestamp()).thenReturn(null);

        assertThatThrownBy(() -> classUnderTest.mapToDomain(emissionsData))
                .isInstanceOf(InvariantException.class)
                .hasMessageContaining("OptimalTime should not be null");

    }

}