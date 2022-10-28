package de.envite.greenbpm.carbonreductorconnector.service;

import de.envite.greenbpm.carbonreductorconnector.TestDataGenerator;
import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductorconnector.domain.service.CarbonReductorException;
import de.envite.greenbpm.carbonreductorconnector.domain.service.DelayCalculatorService;
import de.envite.greenbpm.carbonreductorconnector.usecase.out.CarbonEmissionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;
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
                delayedWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessDuration())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(delayedWorkerExecutionTimeshiftInput);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should execute immediately because energy is optimal now")
    void shouldCalculateNoDelayBecauseEnergyIsOptimal() throws Exception {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getEmissionTimeframe(immediateWorkerExecutionTimeshiftInput.getLocation(),
                immediateWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessDuration())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(immediateWorkerExecutionTimeshiftInput);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should delay execution for greener energy in 3 hours")
    void shouldCalculateDelayForGreenerEnergyIn3Hours() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(immediateWorkerExecutionTimeshiftInput.getLocation(),
                immediateWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessDuration())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(immediateWorkerExecutionTimeshiftInput);

        assertThat(result.getDelay().isExecutionDelayed()).isTrue();
        assertThat(result.getDelay().getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        assertThat(result.getActualCarbon().getValue()).isEqualTo(0.0);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isCloseTo(100.0, offset(0.1));
    }

    @Test
    @DisplayName("should execute immediately with besser emission in 3 hours but timeshift window has passed")
    void shouldCalculateNoDelayForDelayedWorkerExecution() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(delayedWorkerExecutionTimeshiftInput.getLocation(),
                delayedWorkerExecutionTimeshiftInput.getTimeshiftWindow(), delayedWorkerExecutionTimeshiftInput.getRemainingProcessDuration())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(delayedWorkerExecutionTimeshiftInput);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should execute immediately because remainingDuration exceeds timewindow")
    void shouldCalculateNoDelayForRemainingDurationLongerThanTimeWindow() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(timeshiftWindowIsExceededByRemainingDuration.getLocation(),
                timeshiftWindowIsExceededByRemainingDuration.getTimeshiftWindow(), timeshiftWindowIsExceededByRemainingDuration.getRemainingProcessDuration())).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(timeshiftWindowIsExceededByRemainingDuration);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    void shouldCalculateDelayForSLA() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInput.getLocation()),
                any(de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration.class), eq(slaInput.getRemainingProcessDuration()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInput);

        assertThat(result.getDelay().isExecutionDelayed()).isTrue();
        assertThat(result.getDelay().getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        assertThat(result.getActualCarbon().getValue()).isEqualTo(0.0);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isCloseTo(100.0, offset(0.1));
    }

    // TODO
    @Test
    @DisplayName("should calculate no delay for SLA because maximumDuration would be breached")
    void shouldCalculateNoDelayForSLABecauseProcessIsRunningVeryLong() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInputDelayed.getLocation()),
                any(de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration.class), eq(slaInputDelayed.getRemainingProcessDuration()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInputDelayed);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
    }

    @Test
    @DisplayName("should calculate no delay for SLA mode because energy is optimal")
    void shouldCalculateNoDelayForSLABecauseEnergyIsOptimal() throws CarbonReductorException, CarbonEmissionQueryException {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getEmissionTimeframe(eq(slaInput.getLocation()),
                any(de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration.class), eq(slaInput.getRemainingProcessDuration()))).thenReturn(emissionTimeframe);

        CarbonReduction result = classUnderTest.calculateDelay(slaInput);

        assertThat(result.getDelay().isExecutionDelayed()).isFalse();
        assertThat(result.getDelay().getDelayedBy()).isZero();
        assertThat(result.getActualCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon().getValue()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon().getValue()).isZero();
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
}
