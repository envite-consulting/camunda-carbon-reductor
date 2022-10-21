package de.envite.greenbpm;

import de.envite.greenbpm.carbonreductorconnector.CarbonReductorConnectorFunction;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZeebeClient
public class ZeebeConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeebeConnectorApplication.class, args);
    }

    @Bean
    public CarbonReductorConnectorFunction carbonReductorConnectorFunction() {
        return new CarbonReductorConnectorFunction();
    }
}
