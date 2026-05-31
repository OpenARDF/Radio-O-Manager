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

    /** Opens a project from disk and makes its path the default save destination. */
    fun open(path: Path): EventProjectFile {
        val projectFile = store.read(path)
        currentProject = projectFile
        currentPath = path
        return projectFile
    }

    /** Saves the current project to a specific path and makes that path current. */
    fun saveAs(path: Path) {
        val projectFile = requireNotNull(currentProject) {
            "Cannot save before a project is open."
        }
        store.write(path, projectFile)
        currentPath = path
    }
}
