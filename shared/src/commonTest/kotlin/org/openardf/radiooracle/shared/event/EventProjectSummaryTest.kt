package org.openardf.radiooracle.shared.event

import org.openardf.radiooracle.shared.domain.RaceBand
import org.openardf.radiooracle.shared.domain.RaceLevel
import org.openardf.radiooracle.shared.domain.RaceType
import org.openardf.radiooracle.shared.domain.ResultStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EventProjectSummaryTest {
    @Test
    fun summarizesRaceCountsForDesktopAndOtherClients() {
        val summary = EventProjectSummary.from(projectFile())

        assertEquals("Summary Race", summary.raceName)
        assertEquals(1, summary.categoryCount)
        assertEquals(2, summary.competitorCount)
        assertEquals(2, summary.readoutCount)
        assertEquals(1, summary.resultCount)
    }

    private fun projectFile(): EventProjectFile =
        EventProjectFile(
            raceData = EventRaceData(
                race = EventRace(
                    id = "race",
                    name = "Summary Race",
                    apiKey = "",
                    startDateTimeIso = "2026-05-31T10:00",
                    raceType = RaceType.CLASSIC,
                    raceLevel = RaceLevel.PRACTICE,
                    raceBand = RaceBand.M80,
                    timeLimitSeconds = 7_200
                ),
                categories = listOf(
                    EventCategoryData(
                        category = EventCategory(
                            id = "category",
                            raceId = "race",
                            name = "M21",
                            isMan = true,
                            maxAge = null,
                            lengthMeters = 0,
                            climbMeters = 0,
                            order = 1,
                            differentProperties = false,
                            raceType = null,
                            raceBand = null,
                            timeLimitSeconds = null,
                            controlPointsString = ""
                        ),
                        controlPoints = emptyList(),
                        competitors = emptyList()
                    )
                ),
                aliases = emptyList(),
                competitorData = listOf(
                    competitorData("one", resultStatus = ResultStatus.OK),
                    competitorData("two", resultStatus = null)
                ),
                unmatchedReadoutData = listOf(readout("unmatched", resultStatus = ResultStatus.NO_RANKING))
            )
        )

    private fun competitorData(id: String, resultStatus: ResultStatus?): EventCompetitorData =
        EventCompetitorData(
            competitorCategory = EventCompetitorCategory(
                competitor = EventCompetitor(
                    id = id,
                    raceId = "race",
                    categoryId = "category",
                    firstName = id,
                    lastName = "Runner",
                    club = "",
                    index = "",
                    isMan = true,
                    birthYear = null,
                    siNumber = null,
                    siRent = false,
                    startNumber = 0,
                    drawnStartTimeSeconds = null
                ),
                category = null
            ),
            readoutData = resultStatus?.let { readout(id, it) }
        )

    private fun readout(id: String, resultStatus: ResultStatus): EventReadoutData =
        EventReadoutData(
            result = EventResult(
                id = "result-$id",
                raceId = "race",
                competitorId = id,
                siNumber = null,
                cardType = 0,
                checkTimeSeconds = null,
                startTimeSeconds = null,
                finishTimeSeconds = null,
                readoutDateTimeIso = "2026-05-31T11:00",
                automaticStatus = true,
                resultStatus = resultStatus,
                points = 0,
                runTimeSeconds = 0,
                modified = false,
                sent = false
            ),
            punches = emptyList()
        )
}
