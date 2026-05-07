package kolskypavel.ardfmanager.backend.room.entity.embeddeds

import androidx.room.Embedded
import androidx.room.Relation
import kolskypavel.ardfmanager.backend.room.entity.Alias
import kolskypavel.ardfmanager.backend.room.entity.Punch
import java.io.Serializable

// Contains information about a punch and its alias (if exists)
data class AliasPunch(
    @Embedded var punch: Punch,
    @Relation(
        parentColumn = "race_id",
        entityColumn = "race_id",
    )
    var aliases: List<Alias>,
) : Serializable {
    val alias: Alias?
        get() = aliases.firstOrNull { it.siCode == punch.siCode }

    // For debugging
    constructor() : this(
        Punch(),
        emptyList()
    )

    constructor(punch: Punch) : this(
        punch = punch,
        aliases = emptyList()
    )

    constructor(punch: Punch, alias: Alias?) : this(
        punch = punch,
        aliases = listOfNotNull(alias)
    )
}
