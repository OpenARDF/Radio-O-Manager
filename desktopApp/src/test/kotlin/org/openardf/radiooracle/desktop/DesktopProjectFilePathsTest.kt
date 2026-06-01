package org.openardf.radiooracle.desktop

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Path

class DesktopProjectFilePathsTest {
    @Test
    fun keepsExistingProjectFileExtension() {
        val path = Path.of("event.rom.json")

        assertEquals(path, DesktopProjectFilePaths.withProjectExtension(path))
    }

    @Test
    fun appendsProjectFileExtensionWhenMissing() {
        assertEquals(
            Path.of("event.rom.json"),
            DesktopProjectFilePaths.withProjectExtension(Path.of("event"))
        )
    }
}
