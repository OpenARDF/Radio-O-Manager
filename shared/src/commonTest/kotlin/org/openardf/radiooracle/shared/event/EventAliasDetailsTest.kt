package org.openardf.radiooracle.shared.event

import org.openardf.radiooracle.shared.domain.RaceBand
import org.openardf.radiooracle.shared.domain.RaceLevel
import org.openardf.radiooracle.shared.domain.RaceType
import kotlin.test.Test
import kotlin.test.assertEquals

class EventAliasDetailsTest {
    @Test
    fun buildsAliasRowsSortedBySiCodeThenName() {
        val rows = EventAliasDetails.from(raceData())

        assertEquals(3, rows.size)
        assertEquals("alias-31-b", rows[0].id)
        assertEquals(31, rows[0].siCode)
        assertEquals("31", rows[0].siCodeText)
        assertEquals("B", rows[0].name)
        assertEquals("alias-31-c", rows[1].id)
        assertEquals("C", rows[1].name)
        assertEquals("alias-32", rows[2].id)
    }

    private fun raceData(): EventRaceData =
        EventRaceData(
            race = EventRace(
                id = "race",
                name = "Alias Race",
                apiKey = "",
                startDateTimeIso = "2026-05-31T10:00",
                raceType = RaceType.CLASSIC,
                raceLevel = RaceLevel.PRACTICE,
                raceBand = RaceBand.M80,
                timeLimitSeconds = 7_200
            ),
            categories = emptyList(),
            aliases = listOf(
                EventAlias(id = "alias-32", raceId = "race", siCode = 32, name = "A"),
                EventAlias(id = "alias-31-c", raceId = "race", siCode = 31, name = "C"),
                EventAlias(id = "alias-31-b", raceId = "race", siCode = 31, name = "B")
            ),
            competitorData = emptyList(),
            unmatchedReadoutData = emptyList()
        )
}
