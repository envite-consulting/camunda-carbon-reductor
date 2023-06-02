package de.envite.greenbpm.carbonreductor.ppm.adapter.out

import de.envite.greenbpm.carbonreductor.ppm.usecase.out.PredicationApiQuery
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration

internal class PredictionApiClient(
    private val webClient: WebClient
): PredicationApiQuery {
    override fun queryRemainingTime(passedFlowNodeIds: List<String>): Duration? {
        return webClient.post()
            .uri("predictRemainingTime")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE )
            .body(BodyInserters.fromValue(passedFlowNodeIds))
            .retrieve()
            .bodyToMono<PredicationResource>()
            .blockOptional()
            .map { Duration.parse(it.remaining_time) }
            .orElse(null)
    }
}

internal data class PredicationResource(
    val remaining_time: String
)