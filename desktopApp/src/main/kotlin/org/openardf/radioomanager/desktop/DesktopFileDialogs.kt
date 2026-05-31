package org.openardf.radioomanager.desktop

import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter
import java.nio.file.Path

/** Project-file path helpers shared by desktop file dialogs and tests. */
object DesktopProjectFilePaths {
    const val PROJECT_EXTENSION = ".rom.json"

    /** Returns a path with the standard Radio-O-Manager desktop project extension. */
    fun withProjectExtension(path: Path): Path =
        if (path.fileName.toString().endsWith(PROJECT_EXTENSION)) {
            path
        } else {
            path.resolveSibling("${path.fileName}$PROJECT_EXTENSION")
        }
}

/** AWT-backed file chooser for desktop `.rom.json` project files. */
object DesktopFileDialogs {
    private val projectFileFilter = FilenameFilter { _, name ->
        name.endsWith(DesktopProjectFilePaths.PROJECT_EXTENSION)
    }

    /** Lets the user choose an existing project file, returning null when cancelled. */
    fun chooseOpenProject(): Path? =
        chooseProjectFile("Open Radio-O-Manager Project", FileDialog.LOAD)

    /** Lets the user choose a save location, returning null when cancelled. */
    fun chooseSaveProject(): Path? =
        chooseProjectFile("Save Radio-O-Manager Project", FileDialog.SAVE)
            ?.let(DesktopProjectFilePaths::withProjectExtension)

    /** Lets the user choose an export-copy location, returning null when cancelled. */
    fun chooseExportProject(): Path? =
        chooseProjectFile("Export Radio-O-Manager Project Copy", FileDialog.SAVE)
            ?.let(DesktopProjectFilePaths::withProjectExtension)

    private fun chooseProjectFile(title: String, mode: Int): Path? {
        val dialog = FileDialog(null as Frame?, title, mode)
        dialog.filenameFilter = projectFileFilter
        dialog.file = "*${DesktopProjectFilePaths.PROJECT_EXTENSION}"
        dialog.isVisible = true

        val directory = dialog.directory ?: return null
        val file = dialog.file ?: return null
        return Path.of(directory, file)
    }
}
