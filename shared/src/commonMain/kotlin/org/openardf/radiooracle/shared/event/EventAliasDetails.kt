package org.openardf.radiooracle.shared.event

/** Shared alias row prepared for desktop and other event-admin surfaces. */
data class EventAliasDetails(
    val id: String,
    val siCode: Int,
    val siCodeText: String,
    val name: String
) {
    companion object {
        /** Builds alias display rows sorted by SI code and then display name. */
        fun from(raceData: EventRaceData): List<EventAliasDetails> =
            raceData.aliases
                .sortedWith(compareBy<EventAlias> { it.siCode }.thenBy { it.name })
                .map { alias ->
                    EventAliasDetails(
                        id = alias.id,
                        siCode = alias.siCode,
                        siCodeText = alias.siCode.toString(),
                        name = alias.name
                    )
                }
    }
}
