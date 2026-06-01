package org.openardf.radiooracle.backend.files.json.adapters;

import com.squareup.moshi.ToJson;
import org.openardf.radiooracle.backend.DataProcessor
import org.openardf.radiooracle.backend.files.json.temps.AliasJson
import org.openardf.radiooracle.backend.files.json.temps.FinalResultsJson
import org.openardf.radiooracle.backend.room.entity.embeddeds.RaceData

/** Moshi adapter for exporting final results from a complete race aggregate. */
class FinalResultJsonAdapter(val dataProcessor: DataProcessor) {
    /** Serializes categories, aliases, and competitor results for final-result JSON export. */
    @ToJson
    fun toJson(raceData: RaceData): FinalResultsJson {
        val categoryAdapter = CategoryJsonAdapter(raceData.race.id)
        val competitorAdapter = CompetitorJsonAdapter(raceData.race, dataProcessor)

        return FinalResultsJson(
            categories = raceData.categories.map { cat -> categoryAdapter.toJson(cat) },
            aliases = raceData.aliases.map { al -> AliasJson(al.siCode, al.name) },
            competitors = raceData.competitorData.map { cd -> competitorAdapter.toJson(cd) },
        )
    }
}
