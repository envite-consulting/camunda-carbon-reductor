package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ZeebeConnectorApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_with_worker() {
        assertThat(context.getBean(CarbonReductorWorker.class)).isNotNull();
    }
}