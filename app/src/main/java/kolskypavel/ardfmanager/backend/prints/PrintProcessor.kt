package kolskypavel.ardfmanager.backend.prints

import android.content.Context
import androidx.preference.PreferenceManager
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import kolskypavel.ardfmanager.R
import kolskypavel.ardfmanager.backend.DataProcessor
import kolskypavel.ardfmanager.backend.helpers.TimeProcessor
import kolskypavel.ardfmanager.backend.room.entity.Race
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.AliasPunch
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.CompetitorData
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ResultData
import kolskypavel.ardfmanager.backend.room.enums.PunchStatus
import kolskypavel.ardfmanager.backend.room.enums.ResultStatus
import kolskypavel.ardfmanager.backend.room.enums.SIRecordType
import kolskypavel.ardfmanager.backend.wrappers.ResultWrapper
import java.lang.ref.WeakReference


class PrintProcessor(context: Context, private val dataProcessor: DataProcessor) {
    private val appContext: WeakReference<Context> = WeakReference(context)
    private var printerReady: Boolean = false
    private var printer: EscPosPrinter? = null

    private fun isPrintingEnabled() {
        if (appContext.get() != null) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext.get()!!)
            val enabled = sharedPref.getBoolean(
                appContext.get()!!.getString(R.string.key_prints_enabled), false
            )
            if (!enabled) {
                return
            }
            val address = sharedPref.getString(
                appContext.get()!!.getString(R.string.key_prints_selected_printer_address), ""
            )
            if (!address.isNullOrEmpty()) {
                val bluetoothAdapter = (appContext.get()
                    ?.getSystemService(android.bluetooth.BluetoothManager::class.java))?.adapter
                if (bluetoothAdapter != null) {
                    printer =
                        EscPosPrinter(
                            BluetoothConnection(bluetoothAdapter.getRemoteDevice(address)),
                            203,
                            58f,
                            32,
                            EscPosCharsetEncoding("windows-1252", 16)
                        )    // TODO: fix charset encoding
                    printerReady = true
                }
            }
        }

    }

    fun disablePrinter() {
        printerReady = false
    }

    // TODO: fix diacritics printing
    private fun print(formatted: String) {

        if (!printerReady) {
            isPrintingEnabled()
        }

        if (printerReady) {
            val version = "ARDF Manager v${dataProcessor.getAppVersion()}"
            printer!!.printFormattedText(formatted + "\n\n[C]${version}", 100)
        }
    }


    //TODO: adjust the max length of the text based on the printer width
    fun printFinishTicket(resultData: ResultData, race: Race) {
        val context = appContext.get()!!
        val competitor = resultData.competitorCategory?.competitor
        val category = resultData.competitorCategory?.category?.name ?: "?"
        val punches = getPunchesFormatted(resultData.punches)
        val compName = "${competitor?.firstName ?: "?"} ${competitor?.lastName ?: ""}"
        val compIndex =
            "${competitor?.index ?: context.getString(R.string.unknown)} SI: ${resultData.result.siNumber ?: "?"}"

        val controls =
            "[R]${resultData.result.points} ${
                context.getString(
                    R.string.general_controls
                )
            }"

        val runTime = "${context.getString(R.string.general_run_time)}: " +
                TimeProcessor.durationToMinuteString(resultData.result.runTime) +
                " ${
                    dataProcessor.resultStatusToShortString(
                        resultData.result.resultStatus
                    )
                }"

        val formatted = "[C]<b>${race.name}</b>\n" +
                "[L]\n" +
                "[L]$compName\n" +
                "[L]$compIndex\n" +
                "[L]$category\n\n" +
                punches + "\n\n" +
                "[R]<b>$runTime</b>\n" +
                "[R]$controls\n"

        print(formatted)
    }

    private fun getPunchesFormatted(punches: List<AliasPunch>): String {
        return punches.joinToString("\n") { p -> getAliasPunchFormatted(p) }
    }

    private fun getAliasPunchFormatted(aliasPunch: AliasPunch): String {
        when (aliasPunch.punch.punchType) {
            SIRecordType.START -> {
                return "[L]${appContext.get()?.getString(R.string.general_start)}" +
                        "[R]${aliasPunch.punch.siTime.getTimeString()}[R] "
            }

            SIRecordType.FINISH -> {
                return "[L]${appContext.get()?.getString(R.string.general_finish)}" +
                        "[R]${aliasPunch.punch.siTime.getTimeString()}" +
                        "[R]${TimeProcessor.durationToMinuteString(aliasPunch.punch.split)}"
            }

            SIRecordType.CONTROL -> {
                return "[L]${formatCodeString(aliasPunch)}" +
                        "[R]${aliasPunch.punch.siTime.getTimeString()}" +
                        "[R]${TimeProcessor.durationToMinuteString(aliasPunch.punch.split)}"
            }

            else -> {
                return ""
            }
        }
    }

    private fun formatCodeString(aliasPunch: AliasPunch): String {
        val context = appContext.get()!!

        val symbol = when (aliasPunch.punch.punchStatus) {
            PunchStatus.VALID -> context.getString(R.string.punch_status_valid)
            PunchStatus.INVALID -> context.getString(R.string.punch_status_invalid)
            PunchStatus.DUPLICATE -> context.getString(R.string.punch_status_duplicate)
            PunchStatus.UNKNOWN -> context.getString(R.string.punch_status_unknown)
        }
        val code = "${aliasPunch.punch.order} (${aliasPunch.alias ?: aliasPunch.punch.siCode})"
        return "$code$symbol"
    }

    fun printResults(results: List<ResultWrapper>, race: Race) {
        val formatted = formatResultsHeader(race) + getResultsFormatted(results)
        print(formatted)
    }

    private fun formatResultsHeader(race: Race): String {
        return "[C]<font size='big'>${race.name}</font>\n" + "[C]${
            TimeProcessor.formatLocalDate(
                race.startDateTime.toLocalDate()
            )
        }" + "\n\n"
    }

    private fun getResultsFormatted(results: List<ResultWrapper>): String {
        val context = appContext.get()!!
        val sb = StringBuilder()

        results.forEachIndexed() { index, result ->
            if (result.category != null) {
                sb.append(formatCategoryHeader(result))

                // Format each competitor's result
                result.subList.forEach { competitorData ->
                    if (competitorData.resultData?.result != null) {

                        // Format the single result
                        sb.append(formatSingleResult(competitorData))
                        sb.append("\n")
                    }
                }
                if (index < results.size - 1) {
                    sb.append("\n")
                }
            }
        }
        return sb.toString()
    }

    private fun formatCategoryHeader(resultWrapper: ResultWrapper): String {
        val catHead = "<b>${resultWrapper.category?.name}</b>"
        return "${catHead}\n" + formatHorizontalLine()
    }

    private fun formatSingleResult(competitorData: CompetitorData): String {
        val result = competitorData.resultData?.result!!
        var place = result.place.toString()
        var runTime = TimeProcessor.durationToMinuteString(result.runTime)
        val name = getMaxName(competitorData.competitorCategory.competitor.getFullName())

        if (result.resultStatus != ResultStatus.VALID) {
            place = "-"
            runTime = dataProcessor.resultStatusToShortString(result.resultStatus)
        }

        return "[L]$place[L]$name[R]$runTime"
    }

    private fun formatHorizontalLine(): String {
        val lineLength = getCharactersPerLine()
        return "[C]${"-".repeat(lineLength)}\n"
    }

    private fun getCharactersPerLine(): Int {
        return if (printer != null) {
            printer!!.printerNbrCharactersPerLine
        } else {
            32 // Default width for ESC/POS printers
        }
    }

    private fun getMaxName(name: String): String {
        val maxLength = getCharactersPerLine()
        return if (name.length > maxLength) {
            name.substring(0, maxLength)
        } else {
            name
        }
    }
}