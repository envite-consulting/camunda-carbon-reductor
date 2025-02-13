package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.skippy.junit5.PredictWithSkippy;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

@PredictWithSkippy
class MilestoneTest {


    @Test
    void should_read_date_of_date() {
        final OffsetDateTime inputDate = OffsetDateTime.of(
                LocalDate.of(2020, 7, 31),
                LocalTime.of(14, 27, 30),
                ZoneId.of("UTC").getRules().getOffset(Instant.now())
        );

        final Milestone result = new Milestone(inputDate);

        assertThat(result.asDate()).isEqualTo(
                OffsetDateTime.of(
                        LocalDate.of(2020, 7, 31),
                        LocalTime.of(14, 27, 30),
                        ZoneId.of("UTC").getRules().getOffset(Instant.now())
                )
        );
    }
}
