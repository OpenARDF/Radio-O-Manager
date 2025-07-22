package kolskypavel.ardfmanager.backend.room.entity.embeddeds

import androidx.room.Embedded
import kolskypavel.ardfmanager.backend.room.entity.ResultService

data class ResultServiceData(
    @Embedded val resultService: ResultService?,
    val resultCount: Int
) {
}