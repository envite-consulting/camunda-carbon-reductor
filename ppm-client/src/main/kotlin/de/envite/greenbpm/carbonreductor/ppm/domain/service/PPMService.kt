package de.envite.greenbpm.carbonreductor.ppm.domain.service

import de.envite.greenbpm.carbonreductor.ppm.usecase.`in`.PPMQuery
import de.envite.greenbpm.carbonreductor.ppm.usecase.out.PredicationApiQuery
import java.time.Duration

internal class PPMService(
    private val predicationApiQuery: PredicationApiQuery
): PPMQuery {

    override fun queryDuration(passedFlowNodeIds: List<String>): Duration {
        return predicationApiQuery.queryRemainingTime(passedFlowNodeIds) ?: throw RuntimeException("TBD")
    }

}
