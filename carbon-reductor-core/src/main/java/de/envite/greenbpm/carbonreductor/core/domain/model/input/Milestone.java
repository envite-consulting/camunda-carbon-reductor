package de.envite.greenbpm.carbonreductor.core.domain.model.input;

import io.github.domainprimitives.type.ValueObject;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.domainprimitives.validation.Constraints.isNotNull;

public class Milestone extends ValueObject<String> {

    private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

    public Milestone(String value) {
        super(value, isNotNull());
    }

    public OffsetDateTime
    asDate() {
        return OffsetDateTime.parse(this.getValue(), DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC));
    }
}
