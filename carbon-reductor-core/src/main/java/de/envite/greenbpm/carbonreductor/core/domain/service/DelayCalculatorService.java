package de.envite.greenbpm.carbonreductor.core.domain.service;

import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.ProcessDuration;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Percentage;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import io.github.domainprimitives.type.ValueObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static de.envite.greenbpm.carbonreductor.core.domain.model.ExceptionHandlingEnum.CONTINUE_ON_EXCEPTION;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelayCalculatorService implements DelayCalculator {

    private final CarbonEmissionQuery carbonEmissionQuery;

    private static final String ERROR_MSG = "Could not query API to get infos about future emissions";

    @Override
    public CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException {
        ProcessDuration timeshiftWindow = calculateTimeshiftWindowForSLA(input);
        EmissionTimeframe emissionTimeframe;

        try {
            emissionTimeframe = carbonEmissionQuery.getEmissionTimeframe(input.getLocation(), timeshiftWindow, input.getRemainingProcessDuration());
        } catch (CarbonEmissionQueryException e) {
            if (CONTINUE_ON_EXCEPTION.equals(input.getExceptionHandling())) {
                log.error(ERROR_MSG, e);
                return new CarbonReduction(new Delay(false, 0L), new Carbon(0.0), new Carbon(0.0), new Percentage(0.0));
            }
            throw new CarbonReductorException(ERROR_MSG, e);
        }

        boolean isDelayNecessary = input.isDelayStillRelevant() && emissionTimeframe.isCleanerEnergyInFuture();
        boolean isGreaterThanMinimumThreshold = input.getThreshold().isGreaterThanMinimumThreshold(
                emissionTimeframe.calculateSavedCarbonDelta()
        );

        final Carbon carbonWithoutOptimization = Optional.ofNullable(emissionTimeframe.getEarliestForecastedValue())
                .map(ValueObject::getValue)
                .map(Carbon::new)
                .orElse(null);

        if (isDelayNecessary && isGreaterThanMinimumThreshold) {
            final long optimalTime = emissionTimeframe.getOptimalTime().asOffsetDateTime().toInstant().toEpochMilli();
            final long delayedBy = optimalTime - OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
            final boolean executionDelayed = !input.isMeasurementOnly();
            return new CarbonReduction(
                    new Delay(executionDelayed, delayedBy),
                    carbonWithoutOptimization,
                    new Carbon(emissionTimeframe.getOptimalValue().getValue()),
                    new Percentage(emissionTimeframe.calculateSavedCarbonPercentage())
                    );
        }
        // execution is optimal currently
        return new CarbonReduction(
                new Delay(false, 0),
                carbonWithoutOptimization,
                new Carbon(emissionTimeframe.getOptimalValue().getValue()),
                new Percentage(0.0)
        );
    }

    ProcessDuration calculateTimeshiftWindowForSLA(CarbonReductorConfiguration input) {
    	// (milestone + maximumDuration) - milestone - remaining - (now - milestone)
    	// lastPossibletEndDateTime = (milestone + maximumDuration)
    	// lastPossibletEndDateTime - remainingDuration - now
    	OffsetDateTime lastPossibleEndDateTime = input.getMilestone().asDate().plus(input.getMaximumProcessDuration().getValue().toMillis(), ChronoUnit.MILLIS);
    	java.time.Duration duration = java.time.Duration.ofMillis(
                lastPossibleEndDateTime
                        .minus(input.getRemainingProcessDuration().getValue().toMillis(), ChronoUnit.MILLIS)
                        .minus(OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli(), ChronoUnit.MILLIS).toInstant().toEpochMilli());
        if (duration.isNegative()) {
            return new ProcessDuration(Duration.ofMillis(0));
        }
        return new ProcessDuration(duration);
    }
}
