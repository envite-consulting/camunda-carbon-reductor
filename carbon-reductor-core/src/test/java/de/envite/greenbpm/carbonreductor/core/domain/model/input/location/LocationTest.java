package de.envite.greenbpm.carbonreductor.core.domain.model.input.location;

import io.github.domainprimitives.validation.InvariantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {

    @Test
    void should_create_if_valid() {
        assertThat(new Location(Locations.GERMANY_WEST_CENTRAL.regionname())).isNotNull();
    }

    @Nested
    class Invariants {

        @Test
        void should_throw_if_null() {
            assertThatThrownBy(() -> new Location(null))
                    .isInstanceOf(InvariantException.class);
        }

        @Test
        void should_throw_if_unknown_location() {
            assertThatThrownBy(() -> new Location("mars"))
                    .isInstanceOf(InvariantException.class)
                    .hasMessageContaining("mars is not a known location");
        }
    }
}