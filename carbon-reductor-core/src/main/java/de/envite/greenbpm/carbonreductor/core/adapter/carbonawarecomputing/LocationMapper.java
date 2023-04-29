package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;

import java.util.Optional;

public class LocationMapper {

    private static final String NOT_PRESENT = null;

    public String mapLocation(Location location) {
        final Optional<Locations> locationValue = Locations.fromText(location.getValue());
        if (locationValue.isEmpty()) {
            return NOT_PRESENT;
        }
        return switch (locationValue.get()) {
            case EUROPE_NORTH, SWEDEN_CENTRAL, NORWAY_EAST -> "no";
            case EUROPE_WEST, FRANCE_SOUTH -> "fe";
            case GERMANY_NORTH, GERMANY_WEST_CENTRAL -> "de";
            case UK_WEST, UK_SOUTH -> "uk";
            case SWITZERLAND_WEST, SWITZERLAND_NORTH -> "ch";
            default -> NOT_PRESENT;
        };
    }
}
