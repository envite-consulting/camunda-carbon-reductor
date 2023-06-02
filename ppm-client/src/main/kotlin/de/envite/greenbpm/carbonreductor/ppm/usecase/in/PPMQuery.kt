package de.envite.greenbpm.carbonreductor.ppm.usecase.`in`

import java.time.Duration

fun interface PPMQuery {

    fun queryDuration(passedFlowNodeIds: List<String>): Duration
}