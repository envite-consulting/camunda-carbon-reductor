package de.envite.greenbpm.carbonreductor.core.technology.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.envite.greenbpm.carbonreductor.core.technology.Constants.EMISSION_TIMEFRAME_NAME;

@Configuration
@EnableCaching
@EnableConfigurationProperties(EmissionTimeframeCacheProperty.class)
@Slf4j
public class CacheConfiguration {

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    @Bean
    public CacheManager caffeinCache(List<CaffeineCache> caffeineCaches) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCaches.forEach(cache -> caffeineCacheManager.registerCustomCache(cache.getName(), cache.getNativeCache()));
        return caffeineCacheManager;
    }

    @Bean
    public CaffeineCache emissionTimeframeCache(EmissionTimeframeCacheProperty emissionTimeframeCacheProperty, Ticker ticker) {
        return new CaffeineCache(EMISSION_TIMEFRAME_NAME,
                Caffeine.newBuilder()
                        .expireAfterWrite(emissionTimeframeCacheProperty.getExpirationTime(), TimeUnit.MILLISECONDS)
                        .maximumSize(emissionTimeframeCacheProperty.getCacheSize())
                        .ticker(ticker)
                        .build()
        );
    }

}
