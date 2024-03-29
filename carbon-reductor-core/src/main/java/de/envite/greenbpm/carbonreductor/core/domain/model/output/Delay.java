package de.envite.greenbpm.carbonreductor.core.domain.model.output;

import io.github.domainprimitives.object.ComposedValueObject;
import lombok.Getter;

@Getter
public class Delay extends ComposedValueObject {

    private final boolean executionDelayed;
    private final long delayedBy;

    public Delay(boolean executionDelayed, long delayedBy) {
        this.executionDelayed = executionDelayed;
        this.delayedBy = delayedBy;
        this.validate();
    }

    @Override
    protected void validate() {
        if (executionDelayed) {
            validateNotNull(delayedBy, "Delayed By Time");
        }
        evaluateValidations();
    }
}
