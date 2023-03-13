package de.envite.greenbpm.carbonreductor.core.usecase.out.cache;

import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class CacheTestConfig {

    @Bean
    FakeTicker fakeTicker() {
        return new FakeTicker();
    }

    @Bean
    @Primary
    public Ticker replaceTicker(FakeTicker fakeTicker) {
        return fakeTicker;
    }
}
