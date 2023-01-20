package de.envite.greenbpm.carbonreductor.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(CarbonReductorConfiguration.class)
@Configuration
public @interface EnableCarbonReductorCore {
}
