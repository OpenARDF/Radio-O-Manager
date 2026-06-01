package org.openardf.radiooracle.backend.room.entity.embeddeds

import androidx.room.Embedded
import androidx.room.Relation
import org.openardf.radiooracle.backend.room.entity.Alias
import org.openardf.radiooracle.backend.room.entity.ControlPoint
import java.io.Serializable

/** Room relation aggregate for a control point and its optional alias. */
data class ControlPointAlias(
    @Embedded var controlPoint: ControlPoint,
    @Relation(
        parentColumn = "si_code",
        entityColumn = "si_code",
    )
    var alias: Alias?,
) : Serializable
