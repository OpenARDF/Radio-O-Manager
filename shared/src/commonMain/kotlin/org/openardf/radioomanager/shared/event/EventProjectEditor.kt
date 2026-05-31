package org.openardf.radioomanager.shared.event

/** Shared event-project editing helpers used by desktop and future non-Android flows. */
object EventProjectEditor {
    /** Returns a copy of the project file with a validated race name. */
    fun renameRace(projectFile: EventProjectFile, name: String): EventProjectFile {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty()) {
            "Race name cannot be blank."
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(
                race = projectFile.raceData.race.copy(name = trimmedName)
            )
        )
    }
}
