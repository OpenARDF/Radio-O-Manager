package org.openardf.radioomanager.desktop

import java.nio.file.Path

/** Deferred operation that first needs a save/discard/cancel decision for dirty project state. */
sealed interface PendingDirtyProjectAction {
    /** Open the selected project after the user decides what to do with unsaved edits. */
    data class OpenProject(val path: Path) : PendingDirtyProjectAction

    /** Close the current project after the user decides what to do with unsaved edits. */
    data object CloseProject : PendingDirtyProjectAction

    /** Exit the application after the user decides what to do with unsaved edits. */
    data object ExitApplication : PendingDirtyProjectAction
}

/** Decides whether a project action can run immediately or needs the dirty-project prompt first. */
object DesktopDirtyProjectActions {
    /** Returns the action as pending only when there are unsaved edits to protect. */
    fun pendingActionOrNull(
        hasUnsavedChanges: Boolean,
        action: PendingDirtyProjectAction
    ): PendingDirtyProjectAction? =
        if (hasUnsavedChanges) action else null

    /** Close-project actions discard only when the user chose Discard instead of Save. */
    fun shouldDiscardForClose(saveFirst: Boolean): Boolean = !saveFirst
}
