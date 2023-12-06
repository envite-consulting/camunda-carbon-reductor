package de.envite.greenbpm.carbonreductor.core.domain.model.input.location;

import io.github.domainprimitives.type.ValueObject;
import io.github.domainprimitives.validation.Validation;

import java.util.function.Consumer;

import static io.github.domainprimitives.validation.Constraints.isNotNull;

public class Location extends ValueObject<String> {

    private static Consumer<Validation<String>> isKnowLocation() {
        return val -> val.constraint(Locations.fromText(val.value()).isPresent(), () ->
                String.format("%s is not a known location", val.value())
        );
    }

    public Location(String value) {
        super(value, isNotNull().andThen(isKnowLocation()));
    }
}
