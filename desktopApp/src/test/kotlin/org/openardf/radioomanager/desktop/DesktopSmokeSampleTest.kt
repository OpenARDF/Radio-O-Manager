package org.openardf.radioomanager.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.openardf.radioomanager.shared.event.EventCategoryDetails
import org.openardf.radioomanager.shared.event.EventCompetitorDetails
import org.openardf.radioomanager.shared.event.EventProjectEditor
import org.openardf.radioomanager.shared.event.EventProjectSummary
import org.openardf.radioomanager.shared.event.EventReadoutDetails
import org.openardf.radioomanager.shared.event.EventResultDetails
import java.nio.file.Files
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

    @Test
    fun repositorySmokeSampleCanBeEditedSavedAndReadBack() {
        val source = Path.of("..", "samples", "desktop-smoke.rom.json")
        val target = Files.createTempDirectory("rom-desktop-edit-smoke").resolve("edited.rom.json")

        val original = DesktopProjectFiles.read(source)
        val categoryId = original.raceData.categories.first().category.id
        val removedCategoryId = original.raceData.categories.last().category.id
        val competitorId = original.raceData.competitorData.first().competitorCategory.competitor.id
        val aliasId = original.raceData.aliases.first().id

        val edited = EventProjectEditor.assignCompetitorCategory(
            EventProjectEditor.removeCategory(
                EventProjectEditor.updateAlias(
                    EventProjectEditor.updateCompetitorNumbers(
                        EventProjectEditor.renameCompetitor(
                            EventProjectEditor.renameCategory(
                                EventProjectEditor.renameRace(original, "Edited Smoke Race"),
                                categoryId,
                                "M21E"
                            ),
                            competitorId,
                            "Edited",
                            "Runner"
                        ),
                        competitorId,
                        "501",
                        "7654321"
                    ),
                    aliasId,
                    "40",
                    "F4"
                ),
                removedCategoryId,
                deleteCompetitors = false
            ),
            competitorId,
            null
        )

        DesktopProjectFiles.write(target, edited)
        val readBack = DesktopProjectFiles.read(target)

        assertEquals("Edited Smoke Race", readBack.raceData.race.name)
        assertEquals(listOf(categoryId), readBack.raceData.categories.map { it.category.id })
        assertEquals("M21E", readBack.raceData.categories.first { it.category.id == categoryId }.category.name)
        assertEquals(
            null,
            readBack.raceData.competitorData
                .first { it.competitorCategory.competitor.id != competitorId }
                .competitorCategory.competitor.categoryId
        )
        assertEquals(
            "Edited",
            readBack.raceData.competitorData
                .first { it.competitorCategory.competitor.id == competitorId }
                .competitorCategory.competitor.firstName
        )
        assertEquals(
            null,
            readBack.raceData.competitorData
                .first { it.competitorCategory.competitor.id == competitorId }
                .competitorCategory.competitor.categoryId
        )
        assertEquals(501, readBack.raceData.competitorData.first().competitorCategory.competitor.startNumber)
        assertEquals(7_654_321, readBack.raceData.competitorData.first().competitorCategory.competitor.siNumber)
        assertEquals("F4", readBack.raceData.aliases.first { it.id == aliasId }.name)
    }
}
