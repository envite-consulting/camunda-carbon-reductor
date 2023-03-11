package de.envite.greenbpm.carbonreductor.core.context;

import de.envite.greenbpm.carbonreductor.core.domain.service.DelayCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_and_contain_domain_service() {
        assertThat(context.getBean(DelayCalculatorService.class)).isNotNull();
    }
}
