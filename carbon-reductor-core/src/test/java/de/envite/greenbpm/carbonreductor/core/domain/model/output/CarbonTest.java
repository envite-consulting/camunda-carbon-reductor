package de.envite.greenbpm.carbonreductor.core.domain.model.output;


import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonTest {

    @Test
    void should_create_if_valid() {
        assertThat(new Carbon(1.0)).isNotNull();
    }

    @Nested
    class Invariants {

        @Test
        void should_throw_if_null() {
            assertThatThrownBy(() -> new Carbon(null))
                    .isInstanceOf(InvariantException.class);
        }
    }
}