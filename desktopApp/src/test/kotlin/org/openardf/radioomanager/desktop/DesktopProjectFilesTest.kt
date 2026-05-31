package org.openardf.radioomanager.desktop

import org.junit.Assert.assertEquals
import org.junit.Test
import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import org.openardf.radioomanager.shared.event.EventProjectFile
import org.openardf.radioomanager.shared.event.EventRace
import org.openardf.radioomanager.shared.event.EventRaceData
import java.nio.file.Files

class DesktopProjectFilesTest {
    @Test
    fun writesAndReadsSharedProjectFiles() {
        val directory = Files.createTempDirectory("rom-desktop-project")
        val path = directory.resolve("sample.rom.json")
        val projectFile = EventProjectFile(raceData = raceData())

        DesktopProjectFiles.write(path, projectFile)

        assertEquals(projectFile, DesktopProjectFiles.read(path))
    }

    private fun raceData(): EventRaceData =
        EventRaceData(
            race = EventRace(
                id = "race",
                name = "Desktop File Race",
                apiKey = "",
                startDateTimeIso = "2026-05-31T10:00",
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
}
