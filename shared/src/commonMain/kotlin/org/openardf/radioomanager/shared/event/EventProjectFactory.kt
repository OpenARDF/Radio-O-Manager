package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType

/** Creates shared project-file aggregates for new desktop and future non-Android projects. */
object EventProjectFactory {
    /**
     * Builds an empty event project using the same conservative defaults as Android's new race model.
     *
     * The caller supplies IDs and time text so UI layers can choose platform-specific UUID and clock
     * sources while shared code owns the event defaults and aggregate shape.
     */
    fun createEmptyProject(
        raceId: String,
        raceName: String,
        startDateTimeIso: String
    ): EventProjectFile {
        val trimmedName = raceName.trim()
        require(raceId.isNotBlank()) {
            "Race ID cannot be blank."
        }
        require(trimmedName.isNotEmpty()) {
            "Race name cannot be blank."
        }
        require(startDateTimeIso.isNotBlank()) {
            "Race start date/time cannot be blank."
        }

        return EventProjectFile(
            raceData = EventRaceData(
                race = EventRace(
                    id = raceId,
                    name = trimmedName,
                    apiKey = "",
                    startDateTimeIso = startDateTimeIso,
                    raceType = RaceType.CLASSIC,
                    raceLevel = RaceLevel.PRACTICE,
                    raceBand = RaceBand.M80,
                    timeLimitSeconds = 7_200
                ),
                categories = emptyList(),
                aliases = emptyList(),
                competitorData = emptyList(),
                unmatchedReadoutData = emptyList()
            )
        )
    }
}
