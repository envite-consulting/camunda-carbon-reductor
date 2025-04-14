package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;


import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;
import io.skippy.junit5.PredictWithSkippy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@PredictWithSkippy
class LocationMapperTest {

    private final LocationMapper classUnderTest = new LocationMapper();

    @ParameterizedTest
    @MethodSource("provideLocations")
    void should_map_location(Location location, String expectedResult) {
        final String result = classUnderTest.mapLocation(location);

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideLocations() {
        return Stream.of(
                Arguments.of(Locations.EUROPE_NORTH.asLocation(), "no"),
                Arguments.of(Locations.EUROPE_WEST.asLocation(), "fe"),
                Arguments.of(Locations.FRANCE_SOUTH.asLocation(), "fe"),
                Arguments.of(Locations.FRANCE_CENTRAL.asLocation(), Locations.FRANCE_CENTRAL.regionname()),
                Arguments.of(Locations.GERMANY_NORTH.asLocation(), "de"),
                Arguments.of(Locations.GERMANY_WEST_CENTRAL.asLocation(), Locations.GERMANY_WEST_CENTRAL.regionname()),
                Arguments.of(Locations.UK_SOUTH.asLocation(), "uk"),
                Arguments.of(Locations.UK_WEST.asLocation(), "uk"),
                Arguments.of(Locations.SWITZERLAND_NORTH.asLocation(), Locations.SWITZERLAND_NORTH.regionname()),
                Arguments.of(Locations.SWITZERLAND_WEST.asLocation(), "ch"),
                Arguments.of(Locations.SWEDEN_CENTRAL.asLocation(), "no"),
                Arguments.of(Locations.NORWAY_EAST.asLocation(), "no"),
                Arguments.of(Locations.WEST_US.asLocation(), null)
        );
    }
}