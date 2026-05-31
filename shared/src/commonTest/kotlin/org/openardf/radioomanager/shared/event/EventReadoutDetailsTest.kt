package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import org.openardf.radioomanager.shared.domain.ResultStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EventReadoutDetailsTest {
    @Test
    fun buildsDisplayRowsForMatchedAndUnmatchedReadouts() {
        val rows = EventReadoutDetails.from(raceData())

        assertEquals(2, rows.size)
        assertEquals("matched", rows[0].id)
        assertEquals("123456", rows[0].siNumberText)
        assertEquals("RUNNER Alice", rows[0].competitorName)
        assertEquals(ResultStatus.OK, rows[0].resultStatus)
        assertEquals(true, rows[0].automaticStatus)
        assertEquals("OK", rows[0].statusLabel)
        assertEquals("3", rows[0].pointsText)
        assertEquals("00:20:00", rows[0].runTimeText)

        assertEquals("unmatched", rows[1].id)
        assertEquals("654321", rows[1].siNumberText)
        assertEquals("", rows[1].competitorName)
        assertEquals(ResultStatus.NO_RANKING, rows[1].resultStatus)
        assertEquals(true, rows[1].automaticStatus)
        assertEquals("No ranking", rows[1].statusLabel)
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
                name = "Readout Race",
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
                    readoutData = readout("matched", competitor.id, 123456, ResultStatus.OK)
                )
            ),
            unmatchedReadoutData = listOf(readout("unmatched", competitorId = null, siNumber = 654321, ResultStatus.NO_RANKING))
        )
    }

    private fun readout(
        id: String,
        competitorId: String?,
        siNumber: Int,
        resultStatus: ResultStatus
    ): EventReadoutData =
        EventReadoutData(
            result = EventResult(
                id = id,
                raceId = "race",
                competitorId = competitorId,
                siNumber = siNumber,
                cardType = 10,
                checkTimeSeconds = null,
                startTimeSeconds = 600,
                finishTimeSeconds = 1_800,
                readoutDateTimeIso = "2026-05-31T11:00",
                automaticStatus = true,
                resultStatus = resultStatus,
                points = 3,
                runTimeSeconds = 1_200,
                modified = false,
                sent = false
            ),
            punches = emptyList()
        )
}
