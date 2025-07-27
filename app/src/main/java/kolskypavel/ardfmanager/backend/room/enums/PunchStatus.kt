package kolskypavel.ardfmanager.backend.room.enums

enum class PunchStatus {
    VALID,
    INVALID,
    DUPLICATE,
    UNKNOWN;

    fun toRobisCode(): String = when(this) {
        VALID     -> "OK"
        INVALID   -> "MP"
        DUPLICATE -> "DP"
        UNKNOWN   -> "AP"
    }
}