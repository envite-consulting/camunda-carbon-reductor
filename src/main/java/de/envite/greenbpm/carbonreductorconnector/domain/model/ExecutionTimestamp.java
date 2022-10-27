package de.envite.greenbpm.carbonreductorconnector.domain.model;

import io.github.domainprimitives.type.ValueObject;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import static io.github.domainprimitives.validation.Constraints.isNotNull;

public class ExecutionTimestamp extends ValueObject<String> {

    private static final String YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSX'[Etc/UTC]'";

    public ExecutionTimestamp(String value) {
        super(value, isNotNull());
    }

    public OffsetDateTime asDate() {
        return OffsetDateTime.parse(this.getValue(), DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC));
    }
}
