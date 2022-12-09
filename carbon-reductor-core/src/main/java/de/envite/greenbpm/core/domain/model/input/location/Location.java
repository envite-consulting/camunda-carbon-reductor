package de.envite.greenbpm.core.domain.model.input.location;

import io.github.domainprimitives.type.ValueObject;

import static io.github.domainprimitives.validation.Constraints.isNotNull;

public class Location extends ValueObject<String> {
    public Location(String value) {
        // TODO: Validate it is one of the Enum values
        super(value, isNotNull());
    }
}
