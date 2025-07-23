package kolskypavel.ardfmanager.backend.prints

import android.content.Context
import androidx.preference.PreferenceManager
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import kolskypavel.ardfmanager.R
import kolskypavel.ardfmanager.backend.helpers.TimeProcessor
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.AliasPunch
import kolskypavel.ardfmanager.backend.room.entity.embeddeds.ResultData
import kolskypavel.ardfmanager.backend.room.enums.SIRecordType
import java.lang.ref.WeakReference


class PrintProcessor(context: Context) {
    private var appContext: WeakReference<Context> = WeakReference(context)
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
                            32
                        )
                    printerReady = true
                }
            }
        }

    }

    fun disablePrinter() {
        printerReady = false
    }

    fun print(formatted: String) {

        if (!printerReady) {
            isPrintingEnabled()
        }

        if (printerReady) {
            printer!!.printFormattedText(formatted)
        }
    }


    fun printFinishTicket(resultData: ResultData) {
        val competitorName = resultData.competitorCategory?.competitor?.firstName ?: "?"
        val category = resultData.competitorCategory?.category?.name ?: "?"
        val resultTime =
            TimeProcessor.durationToMinuteString(resultData.result.runTime) // Adjust field as needed
        val punches = getPunchesFormatted(resultData.punches)

        val formatted = """
[C]<b>MCR KT</b>
[L]
[L]Name: $competitorName
[L]Category: $category
[L]Time: $resultTime
                
$punches
[L]
 """.trimIndent()

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
                return "[L]${aliasPunch.alias ?: aliasPunch.punch.siCode}" +
                        "[R]${aliasPunch.punch.siTime.getTimeString()}" +
                        "[R]${TimeProcessor.durationToMinuteString(aliasPunch.punch.split)}"
            }

            else -> {
                return ""
            }
        }
    }

}