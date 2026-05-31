package org.openardf.radioomanager.desktop

import org.openardf.radioomanager.shared.event.EventProjectFile
import org.openardf.radioomanager.shared.event.EventProjectFileJson
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/** Desktop filesystem adapter for shared `.rom.json` project files. */
object DesktopProjectFiles {
    /** Reads and decodes a project file from the supplied desktop filesystem path. */
    fun read(path: Path): EventProjectFile =
        EventProjectFileJson.decode(Files.readString(path, StandardCharsets.UTF_8))

    /** Encodes and writes a project file, creating parent directories when needed. */
    fun write(path: Path, projectFile: EventProjectFile) {
        path.parent?.let { Files.createDirectories(it) }
        Files.writeString(path, EventProjectFileJson.encode(projectFile), StandardCharsets.UTF_8)
    }
}
