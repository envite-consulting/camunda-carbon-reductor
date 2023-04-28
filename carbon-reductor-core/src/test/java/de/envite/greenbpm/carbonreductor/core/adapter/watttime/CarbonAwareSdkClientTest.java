package de.envite.greenbpm.carbonreductor.core.adapter.watttime;


import de.envite.greenbpm.api.carbonawaresdk.ApiResponse;
import de.envite.greenbpm.api.carbonawaresdk.api.CarbonAwareApi;
import de.envite.greenbpm.api.carbonawaresdk.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
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
import static org.mockito.Mockito.*;

@MockitoSettings
class CarbonAwareSdkClientTest {

    private CarbonAwareSdkClient classUnderTest;

    @Mock
    private CarbonAwareApi carbonAwareApiMock;

    @Mock
    CarbonAwareApiMapper carbonAwareApiMapperMock;

    static class Data {
        final static Location location = Locations.FRANCE_CENTRAL.asLocation();
        final static Timeshift timeshift = new Timeshift("PT5M");
        final static Timeshift executiontime = new Timeshift("PT10M");

        final static EmissionTimeframe emissionTimeframe = new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(3)),
                new Rating(200.6),
                new ForecastedValue(0.0)
        );
    }

    @BeforeEach
    void setUp() {
        classUnderTest = new CarbonAwareSdkClient(carbonAwareApiMock, carbonAwareApiMapperMock);
    }

    @Test
    void should_call_api_and_map() throws Exception {
        EmissionsForecastDTO emissionsForecastDTO = mock(EmissionsForecastDTO.class);
        ApiResponse<List<EmissionsForecastDTO>> apiResponse = new ApiResponse<>(200, null, List.of(emissionsForecastDTO));
        when(carbonAwareApiMock.getCurrentForecastDataWithHttpInfo(
                eq(List.of(Data.location.getValue())), isNull(),
                // TODO Clock now...
                // org.threeten.bp.OffsetDateTime.parse(Data.timeshift.timeshiftFromNow().toString()),
                any(OffsetDateTime.class),
                eq(Data.executiontime.inMinutes()))).thenReturn(apiResponse);
        when(carbonAwareApiMapperMock.mapToDomain(emissionsForecastDTO)).thenReturn(Data.emissionTimeframe);

        EmissionTimeframe result = classUnderTest.getEmissionTimeframe(Data.location, Data.timeshift, Data.executiontime);

        assertThat(result).isEqualTo(Data.emissionTimeframe);
    }

    @Test
    void should_throw_if_no_element_in_list_response() throws Exception {
        ApiResponse<List<EmissionsForecastDTO>> apiResponse = new ApiResponse<>(200, null, List.of());
        when(carbonAwareApiMock.getCurrentForecastDataWithHttpInfo(any(), isNull(), any(), any()))
                .thenReturn(apiResponse);

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.timeshift, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                        .hasMessage("API provided no data");
    }

    @Test
    void should_catch_exception_on_call_fail() throws Exception {
        when(carbonAwareApiMock.getCurrentForecastDataWithHttpInfo(any(), isNull(), any(), any()))
                .thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> classUnderTest.getEmissionTimeframe(Data.location, Data.timeshift, Data.executiontime))
                .isExactlyInstanceOf(CarbonEmissionQueryException.class)
                .hasMessage(RuntimeException.class.getName());
    }
}