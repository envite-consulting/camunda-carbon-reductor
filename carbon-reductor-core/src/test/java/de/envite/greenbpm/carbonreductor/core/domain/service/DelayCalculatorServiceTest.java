package de.envite.greenbpm.carbonreductor.core.domain.service;

import de.envite.greenbpm.carbonreductor.core.TestDataGenerator;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductor.core.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorModes;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@MockitoSettings
class DelayCalculatorServiceTest {

    private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC =
            "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

    private CarbonReductorConfiguration delayedWorkerExecutionTimeshiftInput;
    private CarbonReductorConfiguration immediateWorkerExecutionTimeshiftInput;
    private CarbonReductorConfiguration timeshiftWindowIsExceededByRemainingDuration;
    private CarbonReductorConfiguration slaInput;
    private CarbonReductorConfiguration slaInputDelayed;

    @Mock
    private CarbonEmissionQuery carbonEmissionQuery;

    private DelayCalculatorService classUnderTest;

    @BeforeEach
    void init() {
        classUnderTest = new DelayCalculatorService(carbonEmissionQuery);

        OffsetDateTime timestampDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(1);
        delayedWorkerExecutionTimeshiftInput = TestDataGenerator.createTimeshiftWindowCarbonReductorInput(
                timestampDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));

        OffsetDateTime timestampImmediate = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
        immediateWorkerExecutionTimeshiftInput = TestDataGenerator.createTimeshiftWindowCarbonReductorInput(
                timestampImmediate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));

        OffsetDateTime timestampNow = OffsetDateTime.now(ZoneOffset.UTC);
        timeshiftWindowIsExceededByRemainingDuration = TestDataGenerator.createTimeshiftWindowIsExceededByRemainingDuration(
                timestampNow.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));

        slaInput = TestDataGenerator.createSLABasedCarbonReductorInput(timestampDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));

        OffsetDateTime milestoneDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(12);
        slaInputDelayed = TestDataGenerator.createSLABasedCarbonReductorInput(milestoneDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
    }

    @Test
    @DisplayName("should execute immediately because timeshift window has already passed")
    void shouldCalculateNoDelayBecauseNoLongerRelevant() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(delayedWorkerExecutionTimeshiftInput.getLocation(),
                delayedWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessTimeshift())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(delayedWorkerExecutionTimeshiftInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should execute immediately because energy is optimal now")
    void shouldCalculateNoDelayBecauseEnergyIsOptimal() throws Exception {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getEmissionTimeframe(immediateWorkerExecutionTimeshiftInput.getLocation(),
                immediateWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessTimeshift())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(immediateWorkerExecutionTimeshiftInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should delay execution for greener energy in 3 hours")
    void shouldCalculateDelayForGreenerEnergyIn3Hours() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(immediateWorkerExecutionTimeshiftInput.getLocation(),
                immediateWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessTimeshift())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(immediateWorkerExecutionTimeshiftInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isTrue();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(0.0);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isCloseTo(100.0, offset(0.1));
    }

    @Test
    @DisplayName("should execute immediately with besser emission in 3 hours but timeshift window has passed")
    void shouldCalculateNoDelayForDelayedWorkerExecution() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(delayedWorkerExecutionTimeshiftInput.getLocation(),
                delayedWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessTimeshift())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(delayedWorkerExecutionTimeshiftInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should execute immediately because remainingDuration exceeds timewindow")
    void shouldCalculateNoDelayForRemainingDurationLongerThanTimeWindow() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(timeshiftWindowIsExceededByRemainingDuration.getLocation(),
                timeshiftWindowIsExceededByRemainingDuration.getTimeshiftWindow(), timeshiftWindowIsExceededByRemainingDuration.getRemainingProcessTimeshift())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(timeshiftWindowIsExceededByRemainingDuration);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    void shouldCalculateDelayForSLA() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInput.getLocation()),
                any(Timeshift.class), eq(slaInput.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isTrue();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(0.0);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isCloseTo(100.0, offset(0.1));
    }

    // TODO
    @Test
    @DisplayName("should calculate no delay for SLA because maximumDuration would be breached")
    void shouldCalculateNoDelayForSLABecauseProcessIsRunningVeryLong() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInputDelayed.getLocation()),
                any(Timeshift.class), eq(slaInputDelayed.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInputDelayed);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should calculate no delay for SLA mode because energy is optimal")
    void shouldCalculateNoDelayForSLABecauseEnergyIsOptimal() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInput.getLocation()),
                any(Timeshift.class), eq(slaInput.getRemainingProcessTimeshift()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInput);

        Assertions.assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        Assertions.assertThat(result.getDelay().getDelayedBy()).isZero();
        Assertions.assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        Assertions.assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("should calculate delay duration for SLA")
    @MethodSource("provideDelayDurationInput")
    void shouldCalculateDelayDurationForSLA(CarbonReductorConfiguration configuration, Timeshift expected) {
        Timeshift actual = classUnderTest.calculateTimeshiftWindowForSLA(configuration);
        assertThat(actual.timeshiftFromNow()).isCloseTo(expected.timeshiftFromNow(), within(5, ChronoUnit.SECONDS));
    }

    private static EmissionTimeframe createBetterEmissionTimeframeIn3Hours() {
        return new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now().plusHours(3)),
                new Rating(200.6),
                new ForecastedValue(0.0)
        );
    }

    private static EmissionTimeframe createEmissionTimeframeCurrentlyOptimal() {
        return new EmissionTimeframe(
                new OptimalTime(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)),
                new Rating(200.6),
                new ForecastedValue(200.6)
        );
    }

    private static Stream<Arguments> provideDelayDurationInput() {
        return Stream.of(
                Arguments.of(named("Milestone now - time shift for 9 hours", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(9)))),
                Arguments.of(named("Milestone 3 hours ago - time shift for 6 hours", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(3).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(6)))),
                Arguments.of(named("Milestone 12 hours ago - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(12).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone now - remainingTime & maxTimeshift equal - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone now - remainingTime > maxTimeshift - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        new Timeshift(String.valueOf(Duration.ofHours(6))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(0)))),
                Arguments.of(named("Milestone long ago - do not time shift", new CarbonReductorConfiguration(
                        Locations.SWEDEN_CENTRAL.asLocation(),
                        CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode(),
                        new Milestone(OffsetDateTime.now(ZoneOffset.UTC).minusHours(50).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                        new Timeshift(String.valueOf(Duration.ofHours(3))),
                        new Timeshift(String.valueOf(Duration.ofHours(12))),
                        null
                )), new Timeshift(String.valueOf(Duration.ofHours(0))))
        );
    }
}
