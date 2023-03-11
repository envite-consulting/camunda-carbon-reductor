package de.envite.greenbpm.carbonreductor.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCarbonReductorCore
public class TestContextApp {

    public static void main(String[] args) {
        SpringApplication.run(TestContextApp.class, args);
    }
}
