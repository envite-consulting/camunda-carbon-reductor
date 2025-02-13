package de.envite.greenbpm.carbonreductor.core.domain.model.output;

import io.skippy.junit5.PredictWithSkippy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@PredictWithSkippy
class DelayTest {

    @Test
    void should_create_if_valid() {
        assertThat(new Delay(true, 20L)).isNotNull();
    }

    @Test
    void should_create_if_valid_no_delay() {
        assertThat(new Delay(false, 0L)).isNotNull();
    }
}