package de.envite.greenbpm.carbonreductor.core.usecase.out.cache;

import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiException;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.ApiResponse;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.CarbonAwareApi;
import de.envite.greenbpm.carbonreductor.api.carbon.aware.model.EmissionsForecastDTO;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareApiMapper;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareSdkClient;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import de.envite.greenbpm.carbonreductor.core.technology.cache.CacheConfiguration;
import de.envite.greenbpm.carbonreductor.core.technology.cache.EmissionTimeframeCacheProperty;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static de.envite.greenbpm.carbonreductor.core.technology.Constants.EMISSION_TIMEFRAME_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "carbon-reductor.caching.emission-timeframe.expiration-time=3000",
                "carbon-reductor.caching.emission-timeframe.cache-size=5"
        },
        classes = {
                CacheTestConfig.class,
                CacheConfiguration.class,
                CarbonAwareSdkClient.class,
                CarbonAwareApi.class,
                CarbonAwareApiMapper.class
        })
class CarbonEmissionQueryCacheIntTest {

    @Autowired
    private CarbonEmissionQuery classWithCache;

    @MockBean
    private CarbonAwareApi carbonAwareApiMock;

    @MockBean
    private CarbonAwareApiMapper carbonAwareApiMapperMock;

    @Autowired
    private FakeTicker fakeTicker;

    @Autowired
    private EmissionTimeframeCacheProperty properties;

    @BeforeEach
    void setUp(@Autowired CacheManager cacheManager) throws ApiException {
        cacheManager.getCache(EMISSION_TIMEFRAME_NAME).clear();

        EmissionsForecastDTO emissionsForecastDTO = mock(EmissionsForecastDTO.class);
        ApiResponse<List<EmissionsForecastDTO>> apiResponse = new ApiResponse<>(200, null, List.of(emissionsForecastDTO));

        when(carbonAwareApiMock.getCurrentForecastDataWithHttpInfo(any(), any(), any(), any())).thenReturn(apiResponse);
    }

    static class First {
        final static Location location = Locations.FRANCE_CENTRAL.asLocation();
        final static Timeshift timeshift = new Timeshift("PT5M");
        final static Timeshift executiontime = new Timeshift("PT10M");

        final static EmissionTimeframe emissionTimeframe = new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(3)),
                new Rating(200.6),
                new ForecastedValue(0.0)
        );
    }

    static class Second {
        final static Location location = Locations.GERMANY_NORTH.asLocation();
        final static Timeshift timeshift = new Timeshift("PT15M");
        final static Timeshift executiontime = new Timeshift("PT15M");

        final static EmissionTimeframe emissionTimeframe = new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(5)),
                new Rating(400.6),
                new ForecastedValue(0.5)
        );
    }

    @Test
    void should_cache_if_already_reqeusted() throws Exception {
        when(carbonAwareApiMapperMock.mapToDomain(any()))
                .thenReturn(First.emissionTimeframe)
                .thenReturn(Second.emissionTimeframe);

        IntStream.range(1, 7).forEach(i -> {
            try {
                assertThat(classWithCache.getEmissionTimeframe(First.location, First.timeshift, First.executiontime))
                        .isEqualTo(First.emissionTimeframe);
            } catch (CarbonEmissionQueryException e) {
                throw new RuntimeException(e);
            }
        });
        verify(carbonAwareApiMock, times(1)).getCurrentForecastDataWithHttpInfo(
                any(), isNull(), any(), eq(First.executiontime.inMinutes()));

        IntStream.range(1, 7).forEach(i -> {
            try {
                assertThat(classWithCache.getEmissionTimeframe(Second.location, Second.timeshift, Second.executiontime))
                        .isEqualTo(Second.emissionTimeframe);
            } catch (CarbonEmissionQueryException e) {
                throw new RuntimeException(e);
            }
        });
        verify(carbonAwareApiMock, times(1)).getCurrentForecastDataWithHttpInfo(
                any(), isNull(), any(), eq(First.executiontime.inMinutes()));
        verify(carbonAwareApiMock, times(1)).getCurrentForecastDataWithHttpInfo(
                any(), isNull(), any(), eq(Second.executiontime.inMinutes()));
    }

    @Test
    void should_clear_cache_after_defined_time() throws Exception {
        when(carbonAwareApiMapperMock.mapToDomain(any())).thenReturn(First.emissionTimeframe);

        assertThat(classWithCache.getEmissionTimeframe(First.location, First.timeshift, First.executiontime))
                .isEqualTo(First.emissionTimeframe);

        Duration cacheTime = Duration.of(properties.getExpirationTime(), TimeUnit.MILLISECONDS.toChronoUnit());
        fakeTicker.advance(cacheTime.plusSeconds(100));

        assertThat(classWithCache.getEmissionTimeframe(First.location, First.timeshift, First.executiontime))
                .isEqualTo(First.emissionTimeframe);

        verify(carbonAwareApiMock, times(2))
                .getCurrentForecastDataWithHttpInfo(any(), any(), any(), any());
    }

    @Test
    void should_have_properties() {
        assertThat(properties.getCacheSize()).isEqualTo(5);
        assertThat(properties.getExpirationTime()).isEqualTo(3000);
    }
}