package de.envite.greenbpm.carbonreductor.core.context;

import de.envite.greenbpm.carbonreductor.core.adapter.carbonawarecomputing.CarbonAwareComputingApiClient;
import de.envite.greenbpm.carbonreductor.core.adapter.watttime.CarbonAwareSdkClient;
import de.envite.greenbpm.carbonreductor.core.domain.service.DelayCalculatorService;
import de.envite.greenbpm.carbonreductor.core.usecase.out.CarbonEmissionQuery;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContextTest {

    @Nested
    @SpringBootTest
    class DefaultAPI {

        @Autowired
        private ApplicationContext context;

        @Test
        void should_start_contain_domain_service_and_client() {
            assertThat(context.getBean(DelayCalculatorService.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isInstanceOf(CarbonAwareSdkClient.class);

            assertThatThrownBy(() -> context.getBean(CarbonAwareComputingApiClient.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Nested
    @SpringBootTest(properties = "carbon-reductor.carbon-aware-api.enabled=true")
    class CarbonAwareSdk {

        @Autowired
        private ApplicationContext context;

        @Test
        void should_start_contain_domain_service_and_client() {
            assertThat(context.getBean(DelayCalculatorService.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isInstanceOf(CarbonAwareSdkClient.class);

            assertThatThrownBy(() -> context.getBean(CarbonAwareComputingApiClient.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Nested
    @SpringBootTest(properties = {
            "carbon-reductor.carbon-aware-computing.enabled=true",
            "carbon-reductor.carbon-aware-api.enabled=false"
    })
    class CarbonAwareComputing {

        @Autowired
        private ApplicationContext context;

        @Test
        void should_start_contain_domain_service_and_client() {
            assertThat(context.getBean(DelayCalculatorService.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isNotNull();
            assertThat(context.getBean(CarbonEmissionQuery.class)).isInstanceOf(CarbonAwareComputingApiClient.class);

            assertThatThrownBy(() -> context.getBean(CarbonAwareSdkClient.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }
}
