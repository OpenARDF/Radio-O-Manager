package org.openardf.radiooracle.shared.event

import org.openardf.radiooracle.shared.domain.RaceBand
import org.openardf.radiooracle.shared.domain.RaceLevel
import org.openardf.radiooracle.shared.domain.RaceType
import org.openardf.radiooracle.shared.domain.ResultStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EventResultDetailsTest {
    @Test
    fun buildsDisplayRowsForRankedCompetitorResults() {
        val rows = EventResultDetails.from(raceData())

        assertEquals(1, rows.size)
        assertEquals("result", rows[0].id)
        assertEquals("1", rows[0].placeText)
        assertEquals("RUNNER Alice", rows[0].competitorName)
        assertEquals(ResultStatus.OK, rows[0].resultStatus)
        assertEquals(true, rows[0].automaticStatus)
        assertEquals("OK", rows[0].statusLabel)
        assertEquals("3", rows[0].pointsText)
        assertEquals("00:20:00", rows[0].runTimeText)
    }

    private fun raceData(): EventRaceData {
        val competitor = EventCompetitor(
            id = "competitor",
            raceId = "race",
            categoryId = null,
            firstName = "Alice",
            lastName = "Runner",
            club = "",
            index = "",
            isMan = false,
            birthYear = null,
            siNumber = 123456,
            siRent = false,
            startNumber = 1,
            drawnStartTimeSeconds = null
        )
        return EventRaceData(
            race = EventRace(
                id = "race",
                name = "Result Race",
                apiKey = "",
                startDateTimeIso = "2026-05-31T10:00",
                raceType = RaceType.CLASSIC,
                raceLevel = RaceLevel.PRACTICE,
                raceBand = RaceBand.M80,
                timeLimitSeconds = 7_200
            ),
            categories = emptyList(),
            aliases = emptyList(),
            competitorData = listOf(
                EventCompetitorData(
                    competitorCategory = EventCompetitorCategory(competitor, category = null),
                    readoutData = EventReadoutData(
                        result = EventResult(
                            id = "result",
                            raceId = "race",
                            competitorId = competitor.id,
                            siNumber = 123456,
                            cardType = 10,
                            checkTimeSeconds = null,
                            startTimeSeconds = 600,
                            finishTimeSeconds = 1_800,
                            readoutDateTimeIso = "2026-05-31T11:00",
                            automaticStatus = true,
                            resultStatus = ResultStatus.OK,
                            points = 3,
                            runTimeSeconds = 1_200,
                            modified = false,
                            sent = false,
                            place = 1
                        ),
                        punches = emptyList()
                    )
                )
            ),
            unmatchedReadoutData = emptyList()
        )
    }
}
