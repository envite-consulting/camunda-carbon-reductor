package de.envite.greenbpm.carbonreductor.core.technology.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static de.envite.greenbpm.carbonreductor.core.technology.Constants.CONFIG_PROPERTY_PREFIX;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = CONFIG_PROPERTY_PREFIX + ".caching.emission-timeframe")
public class EmissionTimeframeCacheProperty {

    /**
     * Specifies that each entry should be automatically removed from the cache once a fixed duration in milliseconds
     * has elapsed after the entry's creation, or the most recent replacement of its value.
     */
    private Long expirationTime;

    /**
     * Specifies the maximum number of entries the cache may contain.
     */
    private Long cacheSize;
}
