package de.envite.greenbpm.carbonreductorconnector.domain.service;

import de.envite.greenbpm.carbonreductorconnector.adapter.out.watttime.CarbonEmissionQueryException;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReduction;
import de.envite.greenbpm.carbonreductorconnector.domain.model.EmissionTimeframe;
import de.envite.greenbpm.carbonreductorconnector.domain.model.CarbonReductorConfiguration;
import de.envite.greenbpm.carbonreductorconnector.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Carbon;
import de.envite.greenbpm.carbonreductorconnector.domain.model.output.Delay;
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
    public CarbonReduction calculateDelay(CarbonReductorConfiguration input) throws CarbonReductorException {
        EmissionTimeframe emissionTimeframe;
        if (SLA_BASED_MODE.asCarbonReductorMode().equals(input.getCarbonReductorMode())) {
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

    private Timeshift calculateTimeshiftWindowForSLA(CarbonReductorConfiguration input) {
        // maximumProcessDuration - milestone - remainingDuration - now
        // (milestone + maximumDuration) - milestone - remaining - (now - milestone)
        OffsetDateTime maximumDurationDateTime = input.getMilestone().asDate().plus(input.getMaximumProcessTimeshift().getValue().toMillis(), ChronoUnit.MILLIS);
        long msSinceMilestone = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() - input.getMilestone().asDate().toInstant().toEpochMilli();
        java.time.Duration duration = java.time.Duration.ofMillis(
                maximumDurationDateTime.minus(input.getMilestone().asDate().toInstant().toEpochMilli(), ChronoUnit.MILLIS)
                        .minus(input.getRemainingProcessTimeshift().getValue().toMillis(), ChronoUnit.MILLIS)
                        .minus(msSinceMilestone, ChronoUnit.MILLIS).toInstant().toEpochMilli());
        if (duration.isNegative()) {
            return input.getRemainingProcessTimeshift();
        }
        return new Timeshift(duration);
    }
}
