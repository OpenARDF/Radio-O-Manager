package kolskypavel.ardfmanager.backend.room.entity

import kolskypavel.ardfmanager.backend.room.enums.ResultServiceType
import java.util.UUID

data class ResultService(
    var id: UUID,
    var serviceType: ResultServiceType,
    var raceId: UUID,
    var url: String,
    var enabled: Boolean
) {}