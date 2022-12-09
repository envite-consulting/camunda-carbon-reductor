package de.envite.greenbpm.core.domain.model.input.carbonreductormode;

import io.github.domainprimitives.type.ValueObject;
import io.github.domainprimitives.validation.Constraints;

public class CarbonReductorMode extends ValueObject<String> {

    public CarbonReductorMode(String value) {
        super(value, Constraints.isNotNull());
    }
}
