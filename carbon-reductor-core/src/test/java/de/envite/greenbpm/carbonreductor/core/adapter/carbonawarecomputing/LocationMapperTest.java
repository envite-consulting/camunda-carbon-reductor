package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;


import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationMapperTest {

    private LocationMapper classUnderTets = new LocationMapper();

    @Test
    void should_map_location() {
        Location location = Locations.EUROPE_WEST.asLocation();

        final String result = classUnderTets.mapLocation(location);

        assertThat(result).isEqualTo("de");
    }
}