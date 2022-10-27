package de.envite.greenbpm.carbonreductorconnector.service;

import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorInput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.ForecastedValue;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.OptimalTime;
import de.envite.greenbpm.carbonreductorconnector.domain.model.emissionframe.Rating;
import de.envite.greenbpm.carbonreductorconnector.domain.service.DelayCalculatorService;
import de.envite.greenbpm.carbonreductorconnector.usecase.out.CarbonEmissionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import static de.envite.greenbpm.carbonreductorconnector.TestDataGenerator.createCarbonReductorInput;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.mockito.Mockito.when;

@MockitoSettings
class DelayCalculatorServiceTest {

    private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC =
            "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

    private CarbonReductorInput delayedWorkerExecutionInput;
    private CarbonReductorInput immediateWorkerExecutionInput;

    @Mock
    private CarbonEmissionQuery carbonEmissionQuery;

    private DelayCalculatorService classUnderTest;

    @BeforeEach
    void init() {
        classUnderTest = new DelayCalculatorService(carbonEmissionQuery);

        OffsetDateTime timestampDelayed = OffsetDateTime.now(ZoneOffset.UTC).minusHours(1);
        delayedWorkerExecutionInput = createCarbonReductorInput(
                timestampDelayed.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));

        OffsetDateTime timestampImmediate = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
        immediateWorkerExecutionInput = createCarbonReductorInput(
                timestampImmediate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
    }

    @Test
    void shouldCalculateNoDelayBecauseNoLongerRelevant() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getCurrentEmission(delayedWorkerExecutionInput.getLocation(),
                delayedWorkerExecutionInput.getDuration())).thenReturn(emissionTimeframe);

        CarbonReductorOutput result = classUnderTest.calculateDelay(delayedWorkerExecutionInput);

        assertThat(result.isExecutionDelayed()).isFalse();
        assertThat(result.getDelayedBy()).isZero();
        assertThat(result.getActualCarbon()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon()).isZero();
    }

    @Test
    void shouldCalculateNoDelayBecauseEnergyIsOptimal() throws Exception {
        EmissionTimeframe emissionTimeframe = createEmissionTimeframeCurrentlyOptimal();

        when(carbonEmissionQuery.getCurrentEmission(immediateWorkerExecutionInput.getLocation(),
                immediateWorkerExecutionInput.getDuration())).thenReturn(emissionTimeframe);

        CarbonReductorOutput result = classUnderTest.calculateDelay(immediateWorkerExecutionInput);

        assertThat(result.isExecutionDelayed()).isFalse();
        assertThat(result.getDelayedBy()).isZero();
        assertThat(result.getActualCarbon()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon()).isZero();
    }

    @Test
    void shouldCalculateDelayForGreenerEnergyIn3Hours() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getCurrentEmission(immediateWorkerExecutionInput.getLocation(),
                immediateWorkerExecutionInput.getDuration())).thenReturn(emissionTimeframe);

        CarbonReductorOutput result = classUnderTest.calculateDelay(immediateWorkerExecutionInput);

        assertThat(result.isExecutionDelayed()).isTrue();
        assertThat(result.getDelayedBy()).isGreaterThanOrEqualTo(Duration.ofMinutes(179).toMillis());
        assertThat(result.getActualCarbon()).isEqualTo(0.0);
        assertThat(result.getOriginalCarbon()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon()).isCloseTo(100.0, offset(0.1));
    }

    @Test
    void shouldCalculateNoDelayForDelayedWorkerExecution() throws Exception {
        EmissionTimeframe emissionTimeframe = createBetterEmissionTimeframeIn3Hours();

        when(carbonEmissionQuery.getCurrentEmission(delayedWorkerExecutionInput.getLocation(),
                delayedWorkerExecutionInput.getDuration())).thenReturn(emissionTimeframe);

        CarbonReductorOutput result = classUnderTest.calculateDelay(delayedWorkerExecutionInput);

        assertThat(result.isExecutionDelayed()).isFalse();
        assertThat(result.getDelayedBy()).isZero();
        assertThat(result.getActualCarbon()).isEqualTo(200.6);
        assertThat(result.getOriginalCarbon()).isEqualTo(200.6);
        assertThat(result.getSavedCarbon()).isZero();
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
