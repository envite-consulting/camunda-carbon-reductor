package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.api.carbonawarecomputing.api.ForecastApi;
import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsData;
import de.envite.greenbpm.api.carbonawarecomputing.model.EmissionsForecast;
import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings
class CarbonAwareComputingApiClientTest {

    private CarbonAwareComputingApiClient classUnderTest;

    @Mock
    private ForecastApi forecastApiMock;

    @Mock
    private LocationMapper locationMapperMock;

    @Mock
    private CarbonAwareComputingMapper carbonAwareComputingMapperMock;

    static class Data {
        static final Location location = Locations.FRANCE_CENTRAL.asLocation();
        static final ProcessDuration PROCESS_DURATION = new ProcessDuration("PT5M");
        static final ProcessDuration executiontime = new ProcessDuration("PT10M");

        static final EmissionTimeframe emissionTimeframe = new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(3)),
                new EarliestForecastedValue(200.6),
                new ForecastedValue(0.0)
        );
    }

    private static final String LOCATION = "de";

    @BeforeEach
    void setUp() {
        classUnderTest = new CarbonAwareComputingApiClient(forecastApiMock, locationMapperMock,
                carbonAwareComputingMapperMock);
    }

    @Test
    void should_call_api_and_map() throws Exception {
        when(locationMapperMock.mapLocation(any(Location.class))).thenReturn(LOCATION);
        EmissionsForecast emissionsForecast = mock(EmissionsForecast.class);
        EmissionsData emissionsData = mock(EmissionsData.class);
        when(emissionsForecast.getOptimalDataPoints()).thenReturn(List.of(emissionsData));
        when(forecastApiMock.getBestExecutionTime(
                eq(LOCATION), isNull(), any(OffsetDateTime.class), eq(Data.executiontime.inMinutes()))
        ).thenReturn(List.of(emissionsForecast));
        when(carbonAwareComputingMapperMock.mapToDomain(emissionsData)).thenReturn(Data.emissionTimeframe);

        EmissionTimeframe result = classUnderTest.getEmissionTimeframe(Data.location, Data.PROCESS_DURATION, Data.executiontime);

        assertThat(result).isEqualTo(Data.emissionTimeframe);
    }

    @Test
    void should_throw_if_location_unknown() {
        when(locationMapperMock.mapLocation(any(Location.class))).thenReturn(null);

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.PROCESS_DURATION, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                .hasMessage("The location is not known yet.");
        verifyNoInteractions(forecastApiMock);
        verifyNoInteractions(carbonAwareComputingMapperMock);
    }

    @Test
    void should_throw_if_no_element_in_list_response() throws Exception {
        when(locationMapperMock.mapLocation(any(Location.class))).thenReturn(LOCATION);
        when(forecastApiMock.getBestExecutionTime(
                eq(LOCATION), isNull(), any(OffsetDateTime.class), eq(Data.executiontime.inMinutes()))
        ).thenReturn(List.of());

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.PROCESS_DURATION, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                .hasMessage("API provided no data");
        verifyNoInteractions(carbonAwareComputingMapperMock);
    }

    @Test
    void should_throw_if_no_element_in_inner_list_response() throws Exception {
        when(locationMapperMock.mapLocation(any(Location.class))).thenReturn(LOCATION);
        EmissionsForecast emissionsForecast = mock(EmissionsForecast.class);
        when(emissionsForecast.getOptimalDataPoints()).thenReturn(List.of());
        when(forecastApiMock.getBestExecutionTime(
                eq(LOCATION), isNull(), any(OffsetDateTime.class), eq(Data.executiontime.inMinutes()))
        ).thenReturn(List.of(emissionsForecast));

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.PROCESS_DURATION, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                .hasMessage("API provided no data");
        verifyNoInteractions(carbonAwareComputingMapperMock);
    }

    @Test
    void should_catch_exception_on_call_fail() throws Exception {
        when(locationMapperMock.mapLocation(any(Location.class))).thenReturn(LOCATION);
        when(forecastApiMock.getBestExecutionTime(any(), isNull(), any(), any())).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.PROCESS_DURATION, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                .hasMessage(RuntimeException.class.getName());
    }
}