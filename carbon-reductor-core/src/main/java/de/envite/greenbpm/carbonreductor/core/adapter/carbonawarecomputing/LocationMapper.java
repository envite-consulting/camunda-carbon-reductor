package de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing;

import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location;
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Locations;

public class LocationMapper {

    private static final String NOT_PRESENT = null;

    public String mapLocation(Location location) {
        switch (Locations.fromText(location.getValue()).orElse(null)) {
            case EUROPE_NORTH:
                return "no";
            case EUROPE_WEST:
                return "fe";
            case FRANCE_SOUTH:
                return "fe";
            case GERMANY_NORTH:
                return "de";
            case GERMANY_WEST_CENTRAL:
                return "de";
            case UK_WEST:
                return "uk";
            case UK_SOUTH:
                return "uk";
            case SWITZERLAND_WEST:
                return "ch";
            case SWITZERLAND_NORTH:
                return "ch";
            case SWEDEN_CENTRAL:
                return "no";
            case NORWAY_EAST:
                return "no";
            case WEST_US:
                return NOT_PRESENT;
            default:
                return NOT_PRESENT;
        }
    }
}
