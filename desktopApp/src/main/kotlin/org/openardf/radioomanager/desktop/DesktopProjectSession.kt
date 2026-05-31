package org.openardf.radioomanager.desktop

import org.openardf.radioomanager.shared.event.EventProjectFile
import java.nio.file.Path

/** Storage boundary used by desktop project-session logic. */
interface ProjectFileStore {
    /** Reads a shared project file from a desktop path. */
    fun read(path: Path): EventProjectFile

    /** Writes a shared project file to a desktop path. */
    fun write(path: Path, projectFile: EventProjectFile)
}

/** Tracks the desktop app's currently open project and save location. */
class DesktopProjectSession(private val store: ProjectFileStore) {
    /** Project currently loaded into the desktop app, or null before a file is opened. */
    var currentProject: EventProjectFile? = null
        private set

    /** Filesystem path associated with the current project, or null for unsaved projects. */
    var currentPath: Path? = null
        private set

    /** True after local edits are applied and before those edits are written to disk. */
    var hasUnsavedChanges: Boolean = false
        private set

    /** Opens a project from disk and makes its path the default save destination. */
    fun open(path: Path): EventProjectFile {
        val projectFile = store.read(path)
        currentProject = projectFile
        currentPath = path
        hasUnsavedChanges = false
        return projectFile
    }

    /** Applies a shared project edit to the current project and marks it dirty. */
    fun updateCurrentProject(transform: (EventProjectFile) -> EventProjectFile): EventProjectFile {
        val projectFile = requireNotNull(currentProject) {
            "Cannot edit before a project is open."
        }
        val updatedProject = transform(projectFile)
        currentProject = updatedProject
        hasUnsavedChanges = hasUnsavedChanges || updatedProject != projectFile
        return updatedProject
    }

    /** Saves the current project to its existing path. */
    fun save() {
        val path = requireNotNull(currentPath) {
            "Cannot save before a project path is selected."
        }
        saveAs(path)
    }

    /** Saves the current project to a specific path and makes that path current. */
    fun saveAs(path: Path) {
        val projectFile = requireNotNull(currentProject) {
            "Cannot save before a project is open."
        }
        store.write(path, projectFile)
        currentPath = path
        hasUnsavedChanges = false
    }
}
