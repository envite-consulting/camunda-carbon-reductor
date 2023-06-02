package de.envite.greenbpm.carbonreductor.ppm.usecase.out

import java.time.Duration

fun interface PredicationApiQuery {

    fun queryRemainingTime(passedFlowNodeIds: List<String>): Duration?
}