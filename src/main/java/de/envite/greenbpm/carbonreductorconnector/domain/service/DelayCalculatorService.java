package de.envite.greenbpm.carbonreductorconnector.domain.service;

import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Duration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorOutput;
import de.envite.greenbpm.carbonreductorconnector.usecase.in.DelayCalculator;
import de.envite.greenbpm.carbonreductorconnector.usecase.out.CarbonEmissionQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import static de.envite.greenbpm.carbonreductorconnector.domain.model.input.carbonreductormode.CarbonReductorModes.SLA_BASED_MODE;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelayCalculatorService implements DelayCalculator {
    private final CarbonEmissionQuery carbonEmissionQuery;

    @Override
    public CarbonReductorOutput calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException {
        EmissionTimeframe emissionTimeframe = null;
        if (SLA_BASED_MODE.asCarbonReductorMode().equals(input.getCarbonReductorMode())) {
            // TODO negative duration does not make sense
            Duration timeshiftDuration = calculateTimeshiftWindowForSLA(input);

            try {
                emissionTimeframe = carbonEmissionQuery.getCurrentEmission(input.getLocation(), timeshiftDuration, input.getRemainingProcessDuration());
            } catch (CarbonEmissionQueryException e) {
                throw new CarbonReductorException("Could not query API to get infos about future emissions", e);
            }
        } else {
            try {
                emissionTimeframe = carbonEmissionQuery.getCurrentEmission(input.getLocation(), input.getTimeshiftWindow(), input.getRemainingProcessDuration());
            } catch (CarbonEmissionQueryException e) {
                throw new CarbonReductorException("Could not query API to get infos about future emissions", e);
            }
        }

        boolean isDelayNecessary = input.isDelayStillRelevant() && emissionTimeframe.isCleanerEnergyInFuture();

        if (isDelayNecessary) {
            final long optimalTime = emissionTimeframe.getOptimalTime().asOffsetDateTime().toInstant().toEpochMilli();
            final long delayedBy = optimalTime - OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
            return CarbonReductorOutput.builder()
                    .executionDelayed(true)
                    .delayedBy(delayedBy)
                    .originalCarbon(emissionTimeframe.getRating().getValue())
                    .actualCarbon(emissionTimeframe.getForecastedValue().getValue())
                    .savedCarbon(emissionTimeframe.calculateSavedCarbonPercentage())
                    .build();
        }
        // execution is optimal currently
        return CarbonReductorOutput.builder()
                .executionDelayed(false)
                .delayedBy(0)
                .originalCarbon(emissionTimeframe.getRating().getValue())
                .actualCarbon(emissionTimeframe.getRating().getValue())
                .savedCarbon(0.0)
                .build();
    }

    private Duration calculateTimeshiftWindowForSLA(CarbonReductorConfiguration input) {
        // maximumProcessDuration - milestone - remainingDuration - now
        // (milestone + maximumDuration) - milestone - remaining - (now - milestone)
        OffsetDateTime maximumDurationDateTime = input.getMilestone().asDate().plus(input.getMaximumProcessDuration().asDuration(), ChronoUnit.MILLIS);
        long msSinceMilestone = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() - input.getMilestone().asDate().toInstant().toEpochMilli();
        java.time.Duration duration = java.time.Duration.ofMillis(
                maximumDurationDateTime.minus(input.getMilestone().asDate().toInstant().toEpochMilli(), ChronoUnit.MILLIS)
                        .minus(input.getRemainingProcessDuration().asDuration(), ChronoUnit.MILLIS)
                        .minus(msSinceMilestone, ChronoUnit.MILLIS).toInstant().toEpochMilli());
        if (duration.isNegative()) {
            return input.getRemainingProcessDuration();
        }
        return new Duration(duration.toString());
    }
}
