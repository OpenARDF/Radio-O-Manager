package kolskypavel.ardfmanager.files

import kolskypavel.ardfmanager.backend.files.processors.JsonProcessor
import kolskypavel.ardfmanager.backend.room.entity.Result
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ResultData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream

class JsonExportUnitTest {

    @Test
    fun testJsonResultExport() {

        CoroutineScope(Dispatchers.IO).launch {
            val result = Result()
            val resultData = ResultData(result, emptyList(), null)

            val outputStream = ByteArrayOutputStream()

            JsonProcessor.exportResults(listOf(resultData), outputStream)

            val expected = ""
            assertEquals(expected, outputStream.toString())
        }
    }

    @Test
    fun testJsonRaceExport() {

    }
}