package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import org.openardf.radioomanager.shared.time.DurationFormatter

/** Shared read-only race details prepared for Android and desktop presentation. */
data class EventRaceDetails(
    val name: String,
    val startDateTimeIso: String,
    val raceTypeLabel: String,
    val raceLevelLabel: String,
    val raceBandLabel: String,
    val timeLimitText: String
) {
    companion object {
        /** Builds display-ready race details from portable event race metadata. */
        fun from(race: EventRace): EventRaceDetails =
            EventRaceDetails(
                name = race.name,
                startDateTimeIso = race.startDateTimeIso,
                raceTypeLabel = race.raceType.toDisplayLabel(),
                raceLevelLabel = race.raceLevel.toDisplayLabel(),
                raceBandLabel = race.raceBand.toDisplayLabel(),
                timeLimitText = DurationFormatter.secondsToFormattedString(race.timeLimitSeconds, useMinutes = true)
            )
    }
}

/** English race-type labels matching the existing Android default resources. */
fun RaceType.toDisplayLabel(): String =
    when (this) {
        RaceType.CLASSIC -> "Classic"
        RaceType.SHORT -> "Short"
        RaceType.SPRINT -> "Sprint"
        RaceType.FOXORING -> "Foxoring"
        RaceType.ORIENTEERING -> "Orienteering"
    }

/** English race-level labels matching the existing Android default resources. */
fun RaceLevel.toDisplayLabel(): String =
    when (this) {
        RaceLevel.INTERNATIONAL -> "International"
        RaceLevel.NATIONAL -> "National"
        RaceLevel.REGIONAL -> "Regional"
        RaceLevel.DISTRICT -> "District"
        RaceLevel.PRACTICE -> "Practice"
        RaceLevel.OTHER -> "Other"
    }

/** English race-band labels matching the existing Android default resources. */
fun RaceBand.toDisplayLabel(): String =
    when (this) {
        RaceBand.M80 -> "80m"
        RaceBand.M2 -> "2m"
        RaceBand.COMBINED -> "Combined"
        RaceBand.NONE -> "None"
    }
