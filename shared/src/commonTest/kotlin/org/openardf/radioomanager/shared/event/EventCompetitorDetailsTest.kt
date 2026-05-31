package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import kotlin.test.Test
import kotlin.test.assertEquals

class EventCompetitorDetailsTest {
    @Test
    fun buildsDisplayRowsWithCategoryLookup() {
        val rows = EventCompetitorDetails.from(raceData())

        assertEquals(2, rows.size)
        assertEquals("RUNNER Alice", rows[0].fullName)
        assertEquals("W21", rows[0].categoryName)
        assertEquals("101", rows[0].startNumberText)
        assertEquals("123456", rows[0].siNumberText)

        assertEquals("RUNNER Bob", rows[1].fullName)
        assertEquals("", rows[1].categoryName)
        assertEquals("102", rows[1].startNumberText)
        assertEquals("", rows[1].siNumberText)
    }

    private fun raceData(): EventRaceData {
        val category = EventCategory(
            id = "category",
            raceId = "race",
            name = "W21",
            isMan = false,
            maxAge = null,
            lengthMeters = 5_000,
            climbMeters = 100,
            order = 1,
            differentProperties = false,
            raceType = null,
            raceBand = null,
            timeLimitSeconds = null,
            controlPointsString = ""
        )
        return EventRaceData(
            race = EventRace(
                id = "race",
                name = "Competitor Race",
                apiKey = "",
                startDateTimeIso = "2026-05-31T10:00",
                raceType = RaceType.CLASSIC,
                raceLevel = RaceLevel.PRACTICE,
                raceBand = RaceBand.M80,
                timeLimitSeconds = 7_200
            ),
            categories = listOf(EventCategoryData(category, emptyList(), emptyList())),
            aliases = emptyList(),
            competitorData = listOf(
                competitorData("Alice", categoryId = "category", siNumber = 123456, startNumber = 101),
                competitorData("Bob", categoryId = null, siNumber = null, startNumber = 102)
            ),
            unmatchedReadoutData = emptyList()
        )
    }

    private fun competitorData(
        firstName: String,
        categoryId: String?,
        siNumber: Int?,
        startNumber: Int
    ): EventCompetitorData =
        EventCompetitorData(
            competitorCategory = EventCompetitorCategory(
                competitor = EventCompetitor(
                    id = firstName,
                    raceId = "race",
                    categoryId = categoryId,
                    firstName = firstName,
                    lastName = "Runner",
                    club = "",
                    index = "",
                    isMan = true,
                    birthYear = null,
                    siNumber = siNumber,
                    siRent = false,
                    startNumber = startNumber,
                    drawnStartTimeSeconds = null
                ),
                category = null
            ),
            readoutData = null
        )
}
