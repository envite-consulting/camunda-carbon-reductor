package de.envite.greenbpm.carbonreductorconnector;

import de.envite.greenbpm.carbonreductor.core.EnableCarbonReductorCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCarbonReductorCore
public class CamundaConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaConnectorApplication.class, args);
    }
}
