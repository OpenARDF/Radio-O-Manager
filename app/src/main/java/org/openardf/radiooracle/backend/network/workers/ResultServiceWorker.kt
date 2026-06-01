package org.openardf.radiooracle.backend.network.workers

import android.content.Context
import org.openardf.radiooracle.backend.DataProcessor
import org.openardf.radiooracle.backend.room.entity.Race
import org.openardf.radiooracle.backend.room.entity.ResultService
import okhttp3.OkHttpClient

/** Worker contract implemented by each live-result service provider. */
interface ResultServiceWorker {

    /** Performs optional startup work, such as start-list upload. */
    suspend fun init(
        resultService: ResultService,
        race: Race,
        httpClient: OkHttpClient,
        dataProcessor: DataProcessor,
        context: Context
    )

    /**
     * Exports results with the provided HTTP client.
     *
     * Implementations fetch and update local results as needed and update the service status.
     */
    suspend fun exportResults(
        resultService: ResultService,
        race: Race,
        httpClient: OkHttpClient,
        dataProcessor: DataProcessor,
        context: Context
    )
}
