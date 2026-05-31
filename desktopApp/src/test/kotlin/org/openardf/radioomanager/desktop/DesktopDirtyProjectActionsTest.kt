package org.openardf.radioomanager.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.nio.file.Path

class DesktopDirtyProjectActionsTest {
    @Test
    fun cleanProjectActionsCanRunImmediately() {
        val action = PendingDirtyProjectAction.OpenProject(Path.of("next.rom.json"))

        assertNull(DesktopDirtyProjectActions.pendingActionOrNull(false, action))
    }

    @Test
    fun dirtyProjectActionsBecomePending() {
        val action = PendingDirtyProjectAction.NewProject

        assertEquals(action, DesktopDirtyProjectActions.pendingActionOrNull(true, action))
    }

    @Test
    fun closeDiscardsOnlyWhenUserDoesNotSaveFirst() {
        assertEquals(false, DesktopDirtyProjectActions.shouldDiscardForClose(saveFirst = true))
        assertEquals(true, DesktopDirtyProjectActions.shouldDiscardForClose(saveFirst = false))
    }
}
