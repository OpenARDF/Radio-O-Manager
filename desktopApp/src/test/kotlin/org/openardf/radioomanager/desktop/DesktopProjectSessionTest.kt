package org.openardf.radioomanager.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.openardf.radioomanager.shared.domain.RaceBand
import org.openardf.radioomanager.shared.domain.RaceLevel
import org.openardf.radioomanager.shared.domain.RaceType
import org.openardf.radioomanager.shared.event.EventProjectFile
import org.openardf.radioomanager.shared.event.EventRace
import org.openardf.radioomanager.shared.event.EventRaceData
import java.nio.file.Path

class DesktopProjectSessionTest {
    @Test
    fun startsWithoutAnOpenProject() {
        val session = DesktopProjectSession(InMemoryProjectFileStore())

        assertNull(session.currentProject)
        assertNull(session.currentPath)
    }

    @Test
    fun opensAProjectAndRemembersItsPath() {
        val path = Path.of("event.rom.json")
        val projectFile = projectFile("Opened Race")
        val store = InMemoryProjectFileStore(mapOf(path to projectFile))
        val session = DesktopProjectSession(store)

        session.open(path)

        assertEquals(projectFile, session.currentProject)
        assertEquals(path, session.currentPath)
    }

    @Test
    fun savesCurrentProjectToAChosenPath() {
        val source = Path.of("source.rom.json")
        val target = Path.of("target.rom.json")
        val projectFile = projectFile("Saved Race")
        val store = InMemoryProjectFileStore(mapOf(source to projectFile))
        val session = DesktopProjectSession(store)

        session.open(source)
        session.saveAs(target)

        assertEquals(projectFile, store.writtenProjects[target])
        assertEquals(target, session.currentPath)
    }

    private fun projectFile(name: String): EventProjectFile =
        EventProjectFile(
            raceData = EventRaceData(
                race = EventRace(
                    id = name,
                    name = name,
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
        )
}

private class InMemoryProjectFileStore(
    private val projects: Map<Path, EventProjectFile> = emptyMap()
) : ProjectFileStore {
    val writtenProjects = mutableMapOf<Path, EventProjectFile>()

    override fun read(path: Path): EventProjectFile =
        projects.getValue(path)

    override fun write(path: Path, projectFile: EventProjectFile) {
        writtenProjects[path] = projectFile
    }
}
