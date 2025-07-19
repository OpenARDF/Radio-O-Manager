package kolskypavel.ardfmanager.backend.results

import android.util.Log
import kolskypavel.ardfmanager.backend.DataProcessor
import kolskypavel.ardfmanager.backend.files.processors.JsonProcessor
import kolskypavel.ardfmanager.backend.results.ResultsConstants.JSON
import kolskypavel.ardfmanager.backend.results.ResultsConstants.RESULTS_LOG_TAG
import kolskypavel.ardfmanager.backend.room.entity.ResultService
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ResultData
import kolskypavel.ardfmanager.backend.room.enums.ResultServiceStatus
import kolskypavel.ardfmanager.backend.room.enums.ResultServiceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

object ResultServiceWorker {

    fun resultServiceJob(
        resultService: ResultService,
        dataProcessor: DataProcessor,
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            val httpClient = OkHttpClient.Builder().build()
            while (true) {
                delay(ResultsConstants.RESULT_DELAY)
                when (resultService.serviceType) {
                    ResultServiceType.ROBIS -> exportResultsRobis(
                        dataProcessor,
                        resultService,
                        httpClient
                    )
                }
            }
        }
    }

    private suspend fun exportResultsRobis(
        dataProcessor: DataProcessor,
        resultService: ResultService,
        httpClient: OkHttpClient
    ) {
        // Fetch results and convert them to JSON
        val filteredResults = filterResultDataBySent(
            dataProcessor.getResultDataFlowByRace(resultService.raceId).first()
        )

        // If there are no results to send, return early
        if (filteredResults.isEmpty()) {
            return
        }

        val outStream = ByteArrayOutputStream()
        JsonProcessor.exportResults(outStream, filteredResults)
        val resultString = outStream.toString("UTF-8")
        val body: RequestBody = resultString.toRequestBody(JSON)

        // Send the results to the ROBIS API
        val request: Request = Request.Builder()
            .url(ResultsConstants.ROBIS_API_URL)
            .addHeader("Race-Api-Key", resultService.apiKey)
            .put(body)
            .build()

        httpClient.newCall(request).execute().use { response ->
            when (response.code) {
                in 200..299 -> {
                    // If the response is successful, mark the results as sent
                    updateSentResults(dataProcessor, filteredResults)
                    resultService.status = ResultServiceStatus.OK
                    resultService.sent += filteredResults.size
                }

                401 -> {
                    // Handle unauthorized response
                    resultService.status = ResultServiceStatus.UNAUTHORIZED
                    Log.e(
                        RESULTS_LOG_TAG,
                        "Error ${response.code} sending results to ROBis: ${response.message}"
                    )
                }

                else -> {
                    // Handle error response and log it
                    resultService.status = ResultServiceStatus.ERROR
                    Log.e(
                        RESULTS_LOG_TAG,
                        "Error ${response.code} sending results to ROBis: ${response.message}"
                    )
                }
            }
            updateResultService(dataProcessor, resultService)
        }
    }

    // Filter the results by sent
    private fun filterResultDataBySent(
        resultData: List<ResultData>
    ): List<ResultData> {
        return resultData.filter { !it.result.sent }
    }

    // Mark the results as sent
    private fun updateSentResults(
        dataProcessor: DataProcessor,
        resultData: List<ResultData>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            for (r in resultData) {
                r.result.sent = true
                dataProcessor.createOrUpdateResult(r.result)
            }
        }
    }

    private fun updateResultService(
        dataProcessor: DataProcessor,
        resultService: ResultService
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataProcessor.createOrUpdateResultService(resultService)
        }
    }
}