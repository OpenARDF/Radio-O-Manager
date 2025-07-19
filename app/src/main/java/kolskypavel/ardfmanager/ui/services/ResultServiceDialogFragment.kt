package kolskypavel.ardfmanager.ui.services

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kolskypavel.ardfmanager.R
import kolskypavel.ardfmanager.backend.DataProcessor
import kolskypavel.ardfmanager.backend.results.ResultServiceWorker
import kolskypavel.ardfmanager.backend.room.entity.ResultService
import kolskypavel.ardfmanager.backend.room.enums.ResultServiceType
import kolskypavel.ardfmanager.ui.SelectedRaceViewModel
import kolskypavel.ardfmanager.ui.readouts.ReadoutEditDialogFragmentArgs
import kotlinx.coroutines.runBlocking

class ResultServiceDialogFragment : DialogFragment() {

    private val args: ReadoutEditDialogFragmentArgs by navArgs()
    private lateinit var selectedRaceViewModel: SelectedRaceViewModel
    private val dataProcessor = DataProcessor.get()

    private lateinit var resultService: ResultService

    private lateinit var enableSwitch: SwitchMaterial
    private lateinit var typePicker: MaterialAutoCompleteTextView
    private lateinit var urlInputLayout: TextInputLayout
    private lateinit var urlInput: TextInputEditText
    private lateinit var apiKeyLayout: TextInputLayout
    private lateinit var apiKeyInput: TextInputEditText

    private lateinit var closeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_result_service, container, false)
    }

    private fun DialogFragment.setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.add_dialog)
        setWidthPercent(95)

        val sl: SelectedRaceViewModel by activityViewModels()
        selectedRaceViewModel = sl

        enableSwitch = view.findViewById(R.id.result_service_dialog_enable)
        typePicker = view.findViewById(R.id.result_service_dialog_type)
        urlInputLayout = view.findViewById(R.id.results_service_dialog_url_layout)
        urlInput = view.findViewById(R.id.results_service_dialog_url)
        apiKeyLayout = view.findViewById(R.id.results_service_dialog_api_key_layout)
        apiKeyInput = view.findViewById(R.id.results_service_dialog_api_key)
        closeButton = view.findViewById(R.id.results_service_dialog_close)

        populateFields()
        setButtons()
    }

    private fun populateFields() {
        dialog?.setTitle(R.string.results_service)
        val currRace = selectedRaceViewModel.getCurrentRace()

        runBlocking {
            resultService =
                dataProcessor.getResultServiceByRaceId(currRace.id)
                    ?: ResultService()
        }
        enableSwitch.isChecked = resultService.enabled
        typePicker.setText(
            dataProcessor.resultServiceTypeToString(resultService.serviceType),
            false
        )
        urlInput.setText(resultService.url)
        apiKeyInput.setText(currRace.apiKey)
    }

    private fun setButtons() {
        typePicker.onItemSelectedListener

        enableSwitch.setOnClickListener {
            if (validateFields()) {
                if (!resultService.enabled) {
                    enableResultService()
                } else {
                    disableResultService()
                }

            } else {
                enableSwitch.isEnabled = false
                enableSwitch.isChecked = false
            }
        }

        closeButton.setOnClickListener {
            dialog?.cancel()
        }
    }

    private fun validateFields(): Boolean {
        var valid = true

        val serviceType =
            dataProcessor.resultServiceTypeFromString(typePicker.text.toString())
        val url = urlInput.text.toString()

        when (serviceType) {
            ResultServiceType.ROBIS -> {

                if (apiKeyInput.text.toString().isEmpty()) {
                    valid = false
                    apiKeyLayout.error = getString(R.string.result_service_api_key_missing)
                }
            }
            //TODO: Add more services
        }

        return valid
    }

    private fun enableResultService() {
        runBlocking {
            dataProcessor.setResultServiceJob(
                ResultServiceWorker.resultServiceJob(
                    resultService,
                    dataProcessor
                )
            )
            resultService.enabled = true
            dataProcessor.createOrUpdateResultService(resultService)
        }
    }

    private fun disableResultService() {
        runBlocking {
            dataProcessor.removeResultServiceJob()
            resultService.enabled = false
            dataProcessor.createOrUpdateResultService(resultService)
        }
    }
}