package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class ZeebeConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeebeConnectorApplication.class, args);
    }
}
