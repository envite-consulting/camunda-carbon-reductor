package de.envite.greenbpm.carbonreductor.core.domain.service;

import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.EarliestForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@MockitoSettings
class DelayCalculatorServiceTest {

    private CarbonReductorConfiguration input;
    private CarbonReductorConfiguration inputWithDelay;

    @Mock
    private CarbonEmissionQuery carbonEmissionQuery;

    private DelayCalculatorService classUnderTest;

    @BeforeEach
    void init() {
        classUnderTest = new DelayCalculatorService(carbonEmissionQuery);

        input = new CarbonReductorConfiguration(
                Locations.NORWAY_EAST.asLocation(),
                new Milestone(createTimestamp(1)),
                new Timeshift("PT5H"),
                new Timeshift("PT10H"),
                null,
                null,
                false);

        inputWithDelay = new CarbonReductorConfiguration(
                Locations.NORWAY_EAST.asLocation(),
                new Milestone(createTimestamp(12)),
                new Timeshift("PT5H"),
                new Timeshift("PT10H"),
                null,
                null,
                false);
    }

    private OffsetDateTime createTimestamp(Integer minusHours) {
        OffsetDateTime timestampDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(minusHours);
        return timestampDelayed;
    }

    @ValueSource(booleans = { false, true })
    @ParameterizedTest
    void shouldCalculateDelay(final boolean measurementMode) throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();
        CarbonReductorConfiguration inputConfig = new CarbonReductorConfiguration(
                Locations.NORWAY_EAST.asLocation(),
                new Milestone(createTimestamp(1)),
                new Timeshift("PT5H"),
                new Timeshift("PT10H"),
                null,
                null,
                measurementMode);
        when(carbonEmissionQuery.getEmissionTimeframe(eq(inputConfig.getLocation()),
                any(Timeshift.class), eq(inputConfig.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(inputConfig);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isEqualTo(!measurementMode);
        Assertions.assertThat(result.getDelay().getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        Assertions.assertThat(result.getOptimalForecastedCarbon().getValue()).isEqualTo(0.0);
        Assertions.assertThat(result.getCarbonWithoutOptimization().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbonPercentage().getValue()).isCloseTo(100.0, offset(0.1));
    }

    @Test
    @DisplayName("should calculate no delay because maximumDuration would be breached")
    void shouldCalculateNoDelayBecauseProcessIsRunningVeryLong() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(inputWithDelay.getLocation()),
                any(Timeshift.class), eq(inputWithDelay.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(inputWithDelay);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getOptimalForecastedCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getCarbonWithoutOptimization().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbonPercentage().getValue()).isZero();
    }

    @Test
    @DisplayName("should calculate no delay because energy is optimal")
    void shouldCalculateNoDelayBecauseEnergyIsOptimal() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(input.getLocation()),
                any(Timeshift.class), eq(input.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(input);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getOptimalForecastedCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getCarbonWithoutOptimization().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbonPercentage().getValue()).isZero();
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("should calculate delay duration")
    @MethodSource("provideDelayDurationInput")
    void shouldCalculateDelayDurationForSLA(CarbonReductorConfiguration configuration, Timeshift expected) {
        Timeshift actual = classUnderTest.calculateTimeshiftWindowForSLA(configuration);
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    class ExceptionHandling {

        @Test
        void shouldContinueOnErrorOnCalculateDelay() throws CarbonReductorException, CarbonEmissionQueryException {
            CarbonReductorConfiguration input = new CarbonReductorConfiguration(
                    Locations.NORWAY_EAST.asLocation(),
                    new Milestone(createTimestamp(1)),
                    new Timeshift("PT5H"),
                    new Timeshift("PT10H"),
                    null,
                    ExceptionHandlingEnum.CONTINUE_ON_EXCEPTION,
                    false);

            when(carbonEmissionQuery.getEmissionTimeframe(any(), any(), any()))
                    .thenThrow(new CarbonEmissionQueryException("Test"));

            CarbonReduction result = classUnderTest.calculateDelay(input);

            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
            softAssertions.assertThat(result.getDelay().getDelayedBy()).isZero();
            softAssertions.assertThat(result.getOptimalForecastedCarbon().getValue()).isZero();
            softAssertions.assertThat(result.getCarbonWithoutOptimization().getValue()).isZero();
            softAssertions.assertThat(result.getSavedCarbonPercentage().getValue()).isZero();
            softAssertions.assertAll();
        }

        @Test
        void shouldThrowCustomExceptionOnErrorOnCalculateDelay() throws CarbonEmissionQueryException {
            CarbonReductorConfiguration input = new CarbonReductorConfiguration(
                    Locations.NORWAY_EAST.asLocation(),
                    new Milestone(createTimestamp(1)),
                    new Timeshift("PT5H"),
                    new Timeshift("PT10H"),
                    null,
                    ExceptionHandlingEnum.THROW_BPMN_ERROR,
                    false);
            final Throwable cause = new CarbonEmissionQueryException("Test");

            when(carbonEmissionQuery.getEmissionTimeframe(any(), any(), any()))
                    .thenThrow(cause);

            assertThatThrownBy(() -> classUnderTest.calculateDelay(input))
                    .isInstanceOf(CarbonReductorException.class)
                    .hasMessage("Could not query API to get infos about future emissions")
                    .hasCause(cause);
        }
    }

    private static EmissionTimeframe createBetterEmissionTimeframeIn3Hours() {
        return new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(3)),
                new EarliestForecastedValue(200.6),
                new ForecastedValue(0.0)
        );
    }

    private static EmissionTimeframe createEmissionTimeframeCurrentlyOptimal() {
        return new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)),
                new EarliestForecastedValue(200.6),
                new ForecastedValue(200.6)
        );
    }

    private static Stream<Arguments> provideDelayDurationInput() {
        return Stream.of(
                Arguments.of(named("Milestone irrelevant - time shift for 9 hours", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1)),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(9)))),
                Arguments.of(named("Milestone 3 hours ago - time shift for 6 hours", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now().minusHours(3)),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(6)))),
                Arguments.of(named("Milestone 12 hours ago - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now().minusHours(12)),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone irrelevant - remainingTime & maxTimeshift equal - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1)),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone irrelevant - remainingTime > maxTimeshift - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1)),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        new Timeshift(String.valueOf(Duration.ofHours(6))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone long ago - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        new Milestone(OffsetDateTime.now().minusHours(50)),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null,
                        null,
                        false)),
                        new Timeshift(String.valueOf(Duration.ofHours(0))))
        );
    }
}
