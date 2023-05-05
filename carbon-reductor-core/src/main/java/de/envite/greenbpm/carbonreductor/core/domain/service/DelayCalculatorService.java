package de.envite.greenbpm.carbonreductor.core.domain.service;

import de.envite.greenbpm.carbonreductor.core.adapter.exception.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductor.core.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorModes;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductor.core.domain.model.output.Delay;
import de.envite.greenbpm.carbonreductor.core.usecase.in.DelayCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelayCalculatorService implements DelayCalculator {
    private final CarbonEmissionQuery carbonEmissionQuery;

    @Override
    public CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException {
        EmissionTimeframe emissionTimeframe;
        if (CarbonReductorModes.SLA_BASED_MODE.asCarbonReductorMode().equals(input.getCarbonReductorMode())) {
            // TODO negative duration does not make sense
            Timeshift timeshiftTimeshift = calculateTimeshiftWindowForSLA(input);

            try {
                emissionTimeframe = carbonEmissionQuery.getEmissionTimeframe(input.getLocation(), timeshiftTimeshift, input.getRemainingProcessTimeshift());
            } catch (CarbonEmissionQueryException e) {
                throw new CarbonReductorException("Could not query API to get infos about future emissions", e);
            }
        } else {
            try {
                emissionTimeframe = carbonEmissionQuery.getEmissionTimeframe(input.getLocation(), input.getTimeshiftWindow(), input.getRemainingProcessTimeshift());
            } catch (CarbonEmissionQueryException e) {
                throw new CarbonReductorException("Could not query API to get infos about future emissions", e);
            }
        }

        boolean isDelayNecessary = input.isDelayStillRelevant() && emissionTimeframe.isCleanerEnergyInFuture();

        if (isDelayNecessary) {
            final long optimalTime = emissionTimeframe.getOptimalTime().asOffsetDateTime().toInstant().toEpochMilli();
            final long delayedBy = optimalTime - OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
            return new CarbonReduction(
                    new Delay(true, delayedBy),
                    new Carbon(emissionTimeframe.getRating().getValue()),
                    new Carbon(emissionTimeframe.getForecastedValue().getValue()),
                    new Carbon(emissionTimeframe.calculateSavedCarbonPercentage())
                    );
        }
        // execution is optimal currently
        return new CarbonReduction(
                new Delay(false, 0),
                new Carbon(emissionTimeframe.getRating().getValue()),
                new Carbon(emissionTimeframe.getRating().getValue()),
                new Carbon(0.0)
        );
    }

    Timeshift calculateTimeshiftWindowForSLA(CarbonReductorConfiguration input) {
        // maximumProcessDuration - milestone - remainingDuration - now
        // (milestone + maximumDuration) - milestone - remaining - (now - milestone)
        OffsetDateTime maximumDurationDateTime = input.getMilestone().asDate().plus(input.getMaximumProcessTimeshift().getValue().toMillis(), ChronoUnit.MILLIS);
        long msSinceMilestone = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() - input.getMilestone().asDate().toInstant().toEpochMilli();
        java.time.Duration duration = java.time.Duration.ofMillis(
                maximumDurationDateTime.minus(input.getMilestone().asDate().toInstant().toEpochMilli(), ChronoUnit.MILLIS)
                        .minus(input.getRemainingProcessTimeshift().getValue().toMillis(), ChronoUnit.MILLIS)
                        .minus(msSinceMilestone, ChronoUnit.MILLIS).toInstant().toEpochMilli());
        if (duration.isNegative()) {
            return new Timeshift(Duration.ofMillis(0));
        }
        return new Timeshift(duration);
    }
}
