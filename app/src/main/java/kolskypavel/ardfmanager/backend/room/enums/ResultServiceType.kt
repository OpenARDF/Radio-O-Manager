package kolskypavel.ardfmanager.backend.room.enums

enum class ResultServiceType(val value: Int) {
    ROBIS(0);

    companion object {
        fun getByValue(value: Int) =
            ResultServiceType.entries.firstOrNull { it.value == value } ?: ROBIS
    }
}