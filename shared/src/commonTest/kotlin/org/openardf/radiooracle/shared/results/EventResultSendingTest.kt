package org.openardf.radiooracle.shared.results

import org.openardf.radiooracle.shared.domain.ResultStatus
import org.openardf.radiooracle.shared.event.EventCompetitor
import org.openardf.radiooracle.shared.event.EventCompetitorCategory
import org.openardf.radiooracle.shared.event.EventCompetitorData
import org.openardf.radiooracle.shared.event.EventReadoutData
import org.openardf.radiooracle.shared.event.EventResult
import kotlin.test.Test
import kotlin.test.assertEquals

class EventResultSendingTest {
    @Test
    fun returnsCompetitorIdsWithUnsentReadouts() {
        val results = listOf(
            competitor("missing", readout = null),
            competitor("sent", readout = result(sent = true)),
            competitor("unsent", readout = result(sent = false))
        )

        assertEquals(setOf("unsent"), EventResultSending.unsentCompetitorIds(results))
    }

    private fun competitor(id: String, readout: EventResult?): EventCompetitorData =
        EventCompetitorData(
            competitorCategory = EventCompetitorCategory(
                competitor = EventCompetitor(
                    id = id,
                    raceId = "race",
                    categoryId = null,
                    firstName = id,
                    lastName = "Runner",
                    club = "",
                    index = "",
                    isMan = true,
                    birthYear = null,
                    siNumber = null,
                    siRent = false,
                    startNumber = 1,
                    drawnStartTimeSeconds = null
                ),
                category = null
            ),
            readoutData = readout?.let { EventReadoutData(result = it, punches = emptyList()) }
        )

    private fun result(sent: Boolean): EventResult =
        EventResult(
            id = "result-$sent",
            raceId = "race",
            competitorId = null,
            siNumber = null,
            cardType = 5,
            checkTimeSeconds = null,
            startTimeSeconds = null,
            finishTimeSeconds = null,
            readoutDateTimeIso = "2026-05-30T10:00",
            automaticStatus = true,
            resultStatus = ResultStatus.OK,
            points = 0,
            runTimeSeconds = 0,
            modified = false,
            sent = sent
        )
}
