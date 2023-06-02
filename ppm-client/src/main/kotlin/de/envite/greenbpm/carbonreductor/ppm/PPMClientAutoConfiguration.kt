package de.envite.greenbpm.carbonreductor.ppm

import de.envite.greenbpm.carbonreductor.ppm.adapter.out.PredictionApiClient
import de.envite.greenbpm.carbonreductor.ppm.domain.service.PPMService
import de.envite.greenbpm.carbonreductor.ppm.usecase.`in`.PPMQuery
import de.envite.greenbpm.carbonreductor.ppm.usecase.out.PredicationApiQuery
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConditionalOnClass(PPMQuery::class)
internal class PPMClientAutoConfiguration {

    @Bean
    fun predicationApiClient(predicationApiProperties: PredicationApiProperties): WebClient {
        return WebClient.builder()
            .baseUrl(predicationApiProperties.baseURL)
            .build()
    }

    @Bean
    @ConditionalOnMissingBean
    fun PredicationApiQueryClient(predicationApiClient: WebClient): PredicationApiQuery {
        return PredictionApiClient(predicationApiClient)
    }

    @Bean
    @ConditionalOnMissingBean
    fun ppmService(predicationApiQuery: PredicationApiQuery): PPMQuery {
        return PPMService(predicationApiQuery)
    }
}