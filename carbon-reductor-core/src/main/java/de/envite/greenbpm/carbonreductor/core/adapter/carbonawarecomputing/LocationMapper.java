package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;

import java.util.List;

public class LocationMapper {

    private static final String NOT_PRESENT = null;

    private static final List<Locations> ALREADY_PRESENT_DATA_CENTER_LOCATIONS = List.of(
            Locations.GERMANY_WEST_CENTRAL,
            Locations.SWITZERLAND_NORTH,
            Locations.FRANCE_CENTRAL
    );

    public String mapLocation(Location location) {
        final Locations locationValue = Locations.fromText(location.getValue()).orElse(null);

        if (ALREADY_PRESENT_DATA_CENTER_LOCATIONS.contains(locationValue)) {
            return location.getValue();
        }

        return switch (locationValue) {
            case EUROPE_NORTH, SWEDEN_CENTRAL, NORWAY_EAST -> "no";
            case EUROPE_WEST, FRANCE_SOUTH -> "fe";
            case GERMANY_NORTH -> "de";
            case UK_WEST, UK_SOUTH -> "uk";
            case SWITZERLAND_WEST -> "ch";
            default -> NOT_PRESENT;
        };
    }
}
