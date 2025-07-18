package kolskypavel.ardfmanager.backend.files.json.temps

import kolskypavel.ardfmanager.backend.room.enums.RaceBand
import kolskypavel.ardfmanager.backend.room.enums.RaceLevel
import kolskypavel.ardfmanager.backend.room.enums.RaceType
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

data class RaceJson(
    val id: UUID,
    val race_name: String,
    val race_id: Long?,
    val race_start: LocalDateTime,
    val race_type: RaceType,
    val race_band: RaceBand,
    val race_level: RaceLevel,
    val race_time_limit: Duration
)