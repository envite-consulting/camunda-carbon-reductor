package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import de.envite.greenbpm.carbonreductor.core.EnableCarbonReductorCore;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@EnableCarbonReductorCore
public class ZeebeConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeebeConnectorApplication.class, args);
    }
}
