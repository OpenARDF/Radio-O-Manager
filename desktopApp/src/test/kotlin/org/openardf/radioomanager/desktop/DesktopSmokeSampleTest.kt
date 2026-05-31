package org.openardf.radioomanager.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.openardf.radioomanager.shared.event.EventCategoryDetails
import org.openardf.radioomanager.shared.event.EventCompetitorDetails
import org.openardf.radioomanager.shared.event.EventProjectSummary
import org.openardf.radioomanager.shared.event.EventReadoutDetails
import org.openardf.radioomanager.shared.event.EventResultDetails
import java.nio.file.Path

class DesktopSmokeSampleTest {
    @Test
    fun repositorySmokeSampleExercisesImplementedDesktopSections() {
        val projectFile = DesktopProjectFiles.read(Path.of("..", "samples", "desktop-smoke.rom.json"))
        val raceData = projectFile.raceData

        assertTrue(projectFile.isSupportedSchema())
        assertEquals("Desktop Smoke Race", EventProjectSummary.from(projectFile).raceName)
        assertEquals(2, EventCategoryDetails.from(raceData).size)
        assertEquals(2, EventCompetitorDetails.from(raceData).size)
        assertEquals(2, EventReadoutDetails.from(raceData).size)
        assertEquals(1, EventResultDetails.from(raceData).size)
    }
}
