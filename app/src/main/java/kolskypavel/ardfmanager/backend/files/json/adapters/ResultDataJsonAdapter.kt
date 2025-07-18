package kolskypavel.ardfmanager.backend.files.json.adapters

import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import kolskypavel.ardfmanager.backend.files.json.temps.ResultDataJson
import kolskypavel.ardfmanager.backend.files.json.temps.ResultPunchJson
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ResultData

class ResultDataJsonAdapter {
    @ToJson
    fun toJson(resultData: ResultData): String {
        // Map punches
        val punchesJson = resultData.punches.map { ap ->
            ResultPunchJson(
                code = ap.alias?.name ?: ap.punch.siCode.toString(),
                control_type = ap.punch.punchType.name,
                punch_status = ap.punch.punchStatus.name,
                split_time = ap.punch.split.toString()
            )
        }

        // Map ResultData to ResultDataJson
        val resultDataJson = ResultDataJson(
            run_time = resultData.result.runTime.toString(),
            place = resultData.result.place,
            controls_num = resultData.result.points,
            result_status = resultData.result.resultStatus.name,
            punches = punchesJson
        )

        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ResultDataJson::class.java)
        return adapter.toJson(resultDataJson)
    }
}