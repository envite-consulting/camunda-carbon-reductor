package de.envite.greenbpm.carbonreductor.core.domain.model;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone.YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonReductorConfigurationTest {

    @Test
    void should_set_default_for_error_handling() {
        CarbonReductorConfiguration config = new CarbonReductorConfiguration(
                Locations.SWEDEN_CENTRAL.asLocation(),
                new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                new Timeshift(String.valueOf(Duration.ofHours(3))),
                new Timeshift(String.valueOf(Duration.ofHours(12))),
                null,
                null
        );

        assertThat(config.getExceptionHandling()).isEqualTo(ExceptionHandlingEnum.CONTINUE_ON_EXCEPTION);
    }

    @Test
    void should_set_not_default_for_error_handling_if_value_is_present() {
        CarbonReductorConfiguration config = new CarbonReductorConfiguration(
                Locations.SWEDEN_CENTRAL.asLocation(),
                new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))),
                new Timeshift(String.valueOf(Duration.ofHours(3))),
                new Timeshift(String.valueOf(Duration.ofHours(12))),
                null,
                ExceptionHandlingEnum.THROW_BPMN_ERROR
        );

        assertThat(config.getExceptionHandling()).isEqualTo(ExceptionHandlingEnum.THROW_BPMN_ERROR);
    }

    @Nested
    class Invariants {

        private final Location location = Locations.SWEDEN_CENTRAL.asLocation();

        private final Milestone milestone = new Milestone(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC)));
        private final Timeshift timeshift = new Timeshift(String.valueOf(Duration.ofHours(3)));

        @Test
        void should_throw_on_missing_location() {
            assertThatThrownBy(() -> new CarbonReductorConfiguration(
                    null,
                    milestone, timeshift, timeshift, null, null)
            ).isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_on_missing_milestone() {
            assertThatThrownBy(() -> new CarbonReductorConfiguration(
                    location, null, timeshift, timeshift, null, null)
            ).isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_on_missing_remaining_process_timeshift() {
            assertThatThrownBy(() -> new CarbonReductorConfiguration(
                    location, milestone, null, timeshift, null, null)
            ).isInstanceOf(InvariantException.class);
        }
    }
}