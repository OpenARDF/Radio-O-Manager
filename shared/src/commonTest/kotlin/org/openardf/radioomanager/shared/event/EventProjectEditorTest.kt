package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventProjectEditorTest {
    @Test
    fun renamesRaceWithoutChangingOtherProjectData() {
        val original = projectFile("Original Race")

        val renamed = EventProjectEditor.renameRace(original, " Updated Race ")

        assertEquals("Updated Race", renamed.raceData.race.name)
        assertEquals(original.raceData.race.id, renamed.raceData.race.id)
        assertEquals(original.raceData.categories, renamed.raceData.categories)
        assertEquals(original.schemaVersion, renamed.schemaVersion)
    }

    @Test
    fun rejectsBlankRaceName() {
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameRace(projectFile("Original Race"), "   ")
        }
    }

    @Test
    fun renamesCategoryWithoutChangingOtherCategories() {
        val original = projectFile(
            name = "Original Race",
            categories = listOf(categoryData("cat-1", "M21"), categoryData("cat-2", "W21"))
        )

        val updated = EventProjectEditor.renameCategory(original, "cat-2", " W35 ")

        assertEquals(listOf("M21", "W35"), updated.raceData.categories.map { it.category.name })
        assertEquals(original.raceData.categories[0], updated.raceData.categories[0])
    }

    @Test
    fun rejectsBlankCategoryName() {
        val original = projectFile(categories = listOf(categoryData("cat-1", "M21")))

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCategory(original, "cat-1", " ")
        }
    }

    @Test
    fun rejectsDuplicateCategoryName() {
        val original = projectFile(
            categories = listOf(categoryData("cat-1", "M21"), categoryData("cat-2", "W21"))
        )

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCategory(original, "cat-2", "M21")
        }
    }

    @Test
    fun rejectsUnknownCategoryId() {
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCategory(projectFile(), "missing", "W21")
        }
    }

    @Test
    fun addsCategoryUsingConservativeDefaults() {
        val existingCategory = categoryData("cat-1", "M21")
        val original = projectFile(
            categories = listOf(existingCategory.copy(category = existingCategory.category.copy(order = 4)))
        )

        val updated = EventProjectEditor.addCategory(original, "cat-2", " W21 ")

        val category = updated.raceData.categories.last().category
        assertEquals("cat-2", category.id)
        assertEquals("race", category.raceId)
        assertEquals("W21", category.name)
        assertEquals(5, category.order)
        assertEquals(false, category.differentProperties)
        assertEquals("", category.controlPointsString)
    }

    @Test
    fun rejectsInvalidCategoryAdds() {
        val original = projectFile(
            categories = listOf(categoryData("cat-1", "M21"))
        )

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addCategory(original, "", "W21")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addCategory(original, "cat-2", " ")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addCategory(original, "cat-1", "W21")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addCategory(original, "cat-2", "M21")
        }
    }

    @Test
    fun renamesCompetitorWithoutChangingOtherCompetitors() {
        val original = projectFile(
            competitors = listOf(competitorData("comp-1", "Alice", "Runner"), competitorData("comp-2", "Bob", "Racer"))
        )

        val updated = EventProjectEditor.renameCompetitor(original, "comp-2", " Robert ", " Runner ")

        assertEquals("Alice", updated.raceData.competitorData[0].competitorCategory.competitor.firstName)
        assertEquals("Robert", updated.raceData.competitorData[1].competitorCategory.competitor.firstName)
        assertEquals("Runner", updated.raceData.competitorData[1].competitorCategory.competitor.lastName)
    }

    @Test
    fun rejectsBlankCompetitorFirstName() {
        val original = projectFile(competitors = listOf(competitorData("comp-1", "Alice", "Runner")))

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCompetitor(original, "comp-1", " ", "Runner")
        }
    }

    @Test
    fun rejectsBlankCompetitorLastName() {
        val original = projectFile(competitors = listOf(competitorData("comp-1", "Alice", "Runner")))

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCompetitor(original, "comp-1", "Alice", " ")
        }
    }

    @Test
    fun rejectsUnknownCompetitorId() {
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.renameCompetitor(projectFile(), "missing", "Alice", "Runner")
        }
    }

    @Test
    fun updatesCompetitorNumbersUsingSharedValidationRules() {
        val original = projectFile(
            competitors = listOf(
                competitorData("comp-1", "Alice", "Runner", startNumber = 1, siNumber = 1111),
                competitorData("comp-2", "Bob", "Racer", startNumber = 2, siNumber = 2222)
            )
        )

        val updated = EventProjectEditor.updateCompetitorNumbers(original, "comp-2", " 3 ", " ")

        assertEquals(1, updated.raceData.competitorData[0].competitorCategory.competitor.startNumber)
        assertEquals(3, updated.raceData.competitorData[1].competitorCategory.competitor.startNumber)
        assertEquals(null, updated.raceData.competitorData[1].competitorCategory.competitor.siNumber)
    }

    @Test
    fun rejectsInvalidCompetitorNumberUpdates() {
        val original = projectFile(
            competitors = listOf(
                competitorData("comp-1", "Alice", "Runner", startNumber = 1, siNumber = 1111),
                competitorData("comp-2", "Bob", "Racer", startNumber = 2, siNumber = 2222)
            )
        )

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateCompetitorNumbers(original, "comp-2", "", "3333")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateCompetitorNumbers(original, "comp-2", "1", "3333")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateCompetitorNumbers(original, "comp-2", "3", "999")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateCompetitorNumbers(original, "comp-2", "3", "1111")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateCompetitorNumbers(original, "missing", "3", "3333")
        }
    }

    @Test
    fun updatesAliasUsingSharedValidationRules() {
        val original = projectFile(
            aliases = listOf(alias("alias-1", 31, "F1"), alias("alias-2", 32, "F2"))
        )

        val updated = EventProjectEditor.updateAlias(original, "alias-2", " 33 ", " F3 ")

        assertEquals(31, updated.raceData.aliases[0].siCode)
        assertEquals("F1", updated.raceData.aliases[0].name)
        assertEquals(33, updated.raceData.aliases[1].siCode)
        assertEquals("F3", updated.raceData.aliases[1].name)
    }

    @Test
    fun rejectsInvalidAliasUpdates() {
        val original = projectFile(
            aliases = listOf(alias("alias-1", 31, "F1"), alias("alias-2", 32, "F2"))
        )

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateAlias(original, "alias-2", "31", "F3")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateAlias(original, "alias-2", "33", "TOOLONG")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.updateAlias(original, "missing", "33", "F3")
        }
    }

    @Test
    fun addsAliasUsingSharedValidationRules() {
        val original = projectFile(
            aliases = listOf(alias("alias-1", 31, "F1"))
        )

        val updated = EventProjectEditor.addAlias(original, "alias-2", " 32 ", " F2 ")

        assertEquals(2, updated.raceData.aliases.size)
        assertEquals("race", updated.raceData.aliases[1].raceId)
        assertEquals(32, updated.raceData.aliases[1].siCode)
        assertEquals("F2", updated.raceData.aliases[1].name)
    }

    @Test
    fun rejectsInvalidAliasAdds() {
        val original = projectFile(
            aliases = listOf(alias("alias-1", 31, "F1"))
        )

        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addAlias(original, "", "32", "F2")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addAlias(original, "alias-1", "32", "F2")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addAlias(original, "alias-2", "31", "F2")
        }
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.addAlias(original, "alias-2", "32", "F1")
        }
    }

    @Test
    fun removesAlias() {
        val original = projectFile(
            aliases = listOf(alias("alias-1", 31, "F1"), alias("alias-2", 32, "F2"))
        )

        val updated = EventProjectEditor.removeAlias(original, "alias-1")

        assertEquals(listOf("alias-2"), updated.raceData.aliases.map { it.id })
    }

    @Test
    fun rejectsUnknownAliasRemove() {
        assertFailsWith<IllegalArgumentException> {
            EventProjectEditor.removeAlias(projectFile(), "missing")
        }
    }

    private fun projectFile(
        name: String = "Original Race",
        categories: List<EventCategoryData> = emptyList(),
        competitors: List<EventCompetitorData> = emptyList(),
        aliases: List<EventAlias> = emptyList()
    ): EventProjectFile =
        EventProjectFile(
            raceData = EventRaceData(
                race = EventRace(
                    id = "race",
                    name = name,
                    apiKey = "",
                    startDateTimeIso = "2026-05-31T10:00",
                    raceType = RaceType.CLASSIC,
                    raceLevel = RaceLevel.PRACTICE,
                    raceBand = RaceBand.M80,
                    timeLimitSeconds = 7_200
                ),
                categories = categories,
                aliases = aliases,
                competitorData = competitors,
                unmatchedReadoutData = emptyList()
            )
        )

    private fun categoryData(id: String, name: String): EventCategoryData =
        EventCategoryData(
            category = EventCategory(
                id = id,
                raceId = "race",
                name = name,
                isMan = name.startsWith("M"),
                maxAge = null,
                lengthMeters = 0,
                climbMeters = 0,
                order = 0,
                differentProperties = false,
                raceType = null,
                raceBand = null,
                timeLimitSeconds = null,
                controlPointsString = ""
            ),
            controlPoints = emptyList(),
            competitors = emptyList()
        )

    private fun competitorData(
        id: String,
        firstName: String,
        lastName: String,
        startNumber: Int = 1,
        siNumber: Int? = null
    ): EventCompetitorData =
        EventCompetitorData(
            competitorCategory = EventCompetitorCategory(
                competitor = EventCompetitor(
                    id = id,
                    raceId = "race",
                    categoryId = null,
                    firstName = firstName,
                    lastName = lastName,
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

    private fun alias(id: String, siCode: Int, name: String): EventAlias =
        EventAlias(
            id = id,
            raceId = "race",
            siCode = siCode,
            name = name
        )
}
