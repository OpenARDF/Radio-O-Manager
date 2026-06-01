package org.openardf.radiooracle.shared.course

import org.openardf.radiooracle.shared.domain.ControlPointType

/** Parsed control-point definition with SI code, ARDF role, and sequence order. */
data class ControlPointDefinition(
    /** SportIdent control code. */
    val siCode: Int,
    /** Control role within the event discipline. */
    val type: ControlPointType,
    /** Zero-based course order. */
    val order: Int
)
