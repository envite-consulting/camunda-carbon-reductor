package de.envite.greenbpm.carbonreductorconnector

import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReduction
import de.envite.greenbpm.carbonreductor.core.domain.model.CarbonReductorConfiguration
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Milestone
import de.envite.greenbpm.carbonreductor.core.domain.model.input.Timeshift
import de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode.CarbonReductorMode
import de.envite.greenbpm.carbonreductor.core.domain.model.input.location.Location
import org.joda.time.DateTime
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun mapToDomain(allVariables: Map<String, Any>): CarbonReductorConfiguration {
    return CarbonReductorConfiguration(
        Location(allVariables["location"] as String?),
        CarbonReductorMode(allVariables["carbonReductorMode"] as String?),
        Milestone(getDateTime(allVariables)),
        mapIfNotNull(allVariables["remainingProcessDuration"] as String?),
        mapIfNotNull(allVariables["maximumProcessDuration"] as String?),
        null // Will become relevant in the future
    )
}

private fun getDateTime(allVariables: Map<String, Any>): String {
    val dt = allVariables["milestone"] as DateTime?
    val millis = dt!!.millis
    return Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern(Milestone.YYYY_MM_DD_T_HH_MM_SS_SSSX_ETC_UTC))
}

private fun mapIfNotNull(input: String?): Timeshift? {
    return input?.let { Timeshift(it) }
}

fun mapFromDomain(output: CarbonReduction, allVariables: Map<String, Any>): Map<String, Any> {
    val variables: MutableMap<String, Any> = HashMap()
    variables["executionDelayed"] = output.delay.isExecutionDelayed
    variables["originalCarbon"] = output.originalCarbon.value
    variables["actualCarbon"] = output.actualCarbon.value
    variables["savedCarbon"] = output.savedCarbon.value
    variables["reducedCarbon"] = output.calculateReduction().value
    variables["delayedBy"] = output.delay.delayedBy
    // Override milestone variable because joda time is not a primitive object ..
    variables["milestone"] = getDateTime(allVariables)
    return variables
}
