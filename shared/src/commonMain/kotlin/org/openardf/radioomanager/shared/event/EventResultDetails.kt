package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.time.DurationFormatter

/** Shared read-only result row for competitor result lists. */
data class EventResultDetails(
    val place: Int,
    val placeText: String,
    val competitorName: String,
    val statusLabel: String,
    val pointsText: String,
    val runTimeText: String
) {
    companion object {
        /** Builds display rows for competitors that currently have readout/result data. */
        fun from(raceData: EventRaceData): List<EventResultDetails> =
            raceData.competitorData.mapNotNull { competitorData ->
                val readoutData = competitorData.readoutData ?: return@mapNotNull null
                val result = readoutData.result
                EventResultDetails(
                    place = result.place,
                    placeText = if (result.place > 0) result.place.toString() else "",
                    competitorName = competitorData.competitorCategory.competitor.fullName(),
                    statusLabel = result.resultStatus.toDisplayLabel(),
                    pointsText = result.points.toString(),
                    runTimeText = DurationFormatter.secondsToFormattedString(result.runTimeSeconds, useMinutes = false)
                )
            }.sortedWith(compareBy<EventResultDetails> { if (it.place > 0) it.place else Int.MAX_VALUE }.thenBy { it.competitorName })
    }
}
