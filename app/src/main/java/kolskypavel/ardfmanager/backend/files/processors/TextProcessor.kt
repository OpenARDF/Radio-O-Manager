package kolskypavel.ardfmanager.backend.files.processors

import android.content.Context
import kolskypavel.ardfmanager.R
import kolskypavel.ardfmanager.backend.DataProcessor
import kolskypavel.ardfmanager.backend.files.constants.DataFormat
import kolskypavel.ardfmanager.backend.files.constants.DataType
import kolskypavel.ardfmanager.backend.files.constants.FileConstants
import kolskypavel.ardfmanager.backend.files.wrappers.DataImportWrapper
import kolskypavel.ardfmanager.backend.helpers.TimeProcessor
import kolskypavel.ardfmanager.backend.room.entity.Category
import kolskypavel.ardfmanager.backend.room.entity.ControlPoint
import kolskypavel.ardfmanager.backend.room.entity.Race
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.CompetitorData
import kolskypavel.ardfmanager.backend.wrappers.ResultWrapper
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

object TextProcessor : FormatProcessor {

    override suspend fun importData(
        inStream: InputStream,
        dataType: DataType,
        race: Race,
        dataProcessor: DataProcessor
    ): DataImportWrapper? {
        throw NotImplementedError("Text processor not intended for data import")
    }

    override suspend fun exportData(
        outStream: OutputStream,
        dataType: DataType,
        format: DataFormat,
        dataProcessor: DataProcessor,
        raceId: UUID
    ): Boolean {
        when (dataType) {
            DataType.RESULTS_SIMPLE -> exportSimpleTxtResults(outStream, raceId, dataProcessor)
            else -> {
                TODO()
            }
        }
        return true
    }

    private suspend fun exportSimpleTxtResults(
        outStream: OutputStream,
        raceId: UUID,
        dataProcessor: DataProcessor
    ) {
        val results = dataProcessor.getResultWrapperFlowByRace(raceId).first()
        val params = HashMap<String, String>()

        // Init all the parameters for the template
        initTxtParams(
            dataProcessor,
            dataProcessor.getContext(),
            params,
            results,
            dataProcessor.getRace(raceId)
        )

        val out = TemplateProcessor.processTemplate(
            FileConstants.TEMPLATE_TEXT,
            params
        )

        outStream.write(out.toByteArray())
        outStream.flush()
    }

    private suspend fun initTxtParams(
        dataProcessor: DataProcessor,
        context: Context,
        params: HashMap<String, String>,
        results: List<ResultWrapper>,
        race: Race
    ) {

        params[FileConstants.KEY_TITLE_RESULTS] = context.getString(R.string.general_results)

        params[FileConstants.KEY_TITLE_RACE_NAME] = context.getString(R.string.general_race)
        params[FileConstants.KEY_RACE_NAME] = race.name
        params[FileConstants.KEY_TITLE_RACE_DATE] = context.getString(R.string.general_date)
        params[FileConstants.KEY_RACE_DATE] =
            TimeProcessor.formatLocalDate(race.startDateTime.toLocalDate())
        params[FileConstants.KEY_TITLE_RACE_START_TIME] =
            context.getString(R.string.general_start_time)
        params[FileConstants.KEY_RACE_START_TIME] =
            TimeProcessor.formatLocalTime(race.startDateTime.toLocalTime())
        params[FileConstants.KEY_TITLE_RACE_LEVEL] = context.getString(R.string.general_band)
        params[FileConstants.KEY_RACE_LEVEL] = dataProcessor.raceLevelToString(race.raceLevel)
        params[FileConstants.KEY_TITLE_RACE_BAND] = context.getString(R.string.general_band)
        params[FileConstants.KEY_RACE_BAND] = context.getString(R.string.general_band)

        params[FileConstants.KEY_RACE_RESULTS] =
            generateTxtResults(dataProcessor, context, results, race)

        params[FileConstants.KEY_GENERATED_WITH] =
            context.getString(R.string.results_generated_with)
        params[FileConstants.KEY_VERSION] = dataProcessor.getAppVersion()
    }

    //Generates one line of competitor data
    private fun generateCompetitorData(competitorData: CompetitorData): String {
        val output = ""

        return output
    }


    private fun generateTxtCategoryHeader(
        context: Context,
        category: Category,
        controlPoints: List<ControlPoint>,
        race: Race
    ): String {
        val template = TemplateProcessor.loadTemplate(FileConstants.TEMPLATE_TEXT_CATEGORY, context)
        val params = HashMap<String, String>()

        params[FileConstants.KEY_CAT_NAME] = category.name
        params[FileConstants.KEY_TITLE_LIMIT] = context.getString(R.string.general_limit)
        params[FileConstants.KEY_CAT_LIMIT] = if (category.timeLimit != null) {
            TimeProcessor.durationToMinuteString(category.timeLimit!!)
        } else {
            TimeProcessor.durationToMinuteString(race.timeLimit)
        }

        params[FileConstants.KEY_TITLE_LENGTH] = context.getString(R.string.general_length)
        params[FileConstants.KEY_CAT_LENGTH] = category.length.toString()

        params[FileConstants.KEY_TITLE_CONTROLS] = context.getString(R.string.general_controls)
        params[FileConstants.KEY_CAT_CONTROLS] = controlPoints.size.toString()

        return TemplateProcessor.processTemplate(template, params)
    }

    // Generates the whole result block
    private suspend fun generateTxtResults(
        dataProcessor: DataProcessor,
        context: Context,
        results: List<ResultWrapper>,
        race: Race
    ): String {
        var output = ""

        for (result in results) {
            if (result.category != null) {
                output += generateTxtCategoryHeader(
                    context,
                    result.category,
                    dataProcessor.getControlPointsByCategory(result.category.id),
                    race
                )
                output += "\n"

                for (rd in result.subList) {
                    val competitorData = generateCompetitorData(rd)
                    output += competitorData + "\n"
                }
            }
        }

        return output
    }


}