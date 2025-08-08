
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kolskypavel.ardfmanager.backend.DataProcessor
import kolskypavel.ardfmanager.backend.files.json.temps.ResultJson
import kolskypavel.ardfmanager.backend.files.json.temps.ResultPunchJson
import kolskypavel.ardfmanager.backend.helpers.TimeProcessor
import kolskypavel.ardfmanager.backend.room.entity.Punch
import kolskypavel.ardfmanager.backend.room.entity.Result
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.AliasPunch
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.CompetitorData
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ReadoutData
import kolskypavel.ardfmanager.backend.room.enums.ResultStatus
import kolskypavel.ardfmanager.backend.room.enums.SIRecordType
import kolskypavel.ardfmanager.backend.sportident.SITime
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class ResultJsonAdapter {
    @ToJson
    fun toJson(resultData: CompetitorData): ResultJson {
        val result = resultData.readoutData?.result!!
        return ResultJson(
            run_time = TimeProcessor.durationToMinuteString(result.runTime),
            place = result.place,
            controls_num = result.points,
            result_status = DataProcessor.get()
                .resultStatusToShortString(result.resultStatus),
            punches = resultData.readoutData!!.punches
                .filter { ap -> ap.punch.punchType.name != "START" }
                .map { ap ->
                    val controlType = ap.punch.punchType.name
                    val rawCode = ap.alias?.name ?: ap.punch.siCode.toString()
                    val code = if (controlType == "FINISH" && rawCode == "0") "F" else rawCode

                    ResultPunchJson(
                        code = code,
                        control_type = ap.punch.punchType.name,
                        punch_status = DataProcessor.get()
                            .punchStatusToShortString(ap.punch.punchStatus),
                        real_time = ap.punch.siTime.getTimeString(),
                        week = ap.punch.siTime.getWeek(),
                        day_of_week = ap.punch.siTime.getDayOfWeek(),
                        split_time = TimeProcessor.durationToMinuteString(ap.punch.split)
                    )
                }
        )
    }

    @FromJson
    fun fromJson(json: ResultJson): ReadoutData {
        val result = Result(
            id = UUID.randomUUID(),
            raceId = UUID.randomUUID(), // replace with real value later
            competitorID = null, // will be assigned elsewhere
            categoryId = null,
            siNumber = null, // Not in ResultJson
            cardType = 0, // default/fallback
            checkTime = null,
            origCheckTime = null,
            startTime = null,
            origStartTime = null,
            finishTime = null,
            origFinishTime = null,
            readoutTime = LocalDateTime.now(),
            automaticStatus = false,
            resultStatus = ResultStatus.valueOf(json.result_status), // must match enum exactly
            points = 0,
            runTime = TimeProcessor.minuteStringToDuration(json.run_time),
            modified = false,
            sent = false
        )


        val punches = ArrayList<AliasPunch>()
        json.punches.forEachIndexed { index, punchJson ->

            punches.add(
                AliasPunch(
                    Punch(
                        id = UUID.randomUUID(),
                        raceId = UUID.randomUUID(),
                        resultId = result.id,
                        cardNumber = result.siNumber,
                        siCode = punchJson.code.toInt(),
                        siTime = SITime(
                            LocalTime.parse(punchJson.real_time),
                            punchJson.day_of_week,
                            punchJson.week
                        ),
                        origSiTime = SITime(
                            LocalTime.parse(punchJson.real_time),
                            punchJson.day_of_week,
                            punchJson.week
                        ),
                        punchType = SIRecordType.valueOf(punchJson.control_type),
                        order = index,
                        punchStatus = DataProcessor.get()
                            .shortStringToPunchStatus(punchJson.punch_status),
                        split = TimeProcessor.minuteStringToDuration(punchJson.split_time),
                    ), null
                )
            )
        }

        return ReadoutData(result, punches)
    }
}