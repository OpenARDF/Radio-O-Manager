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
import kolskypavel.ardfmanager.ui.SelectedRaceViewModel
import kolskypavel.ardfmanager.ui.readouts.ReadoutEditDialogFragmentArgs

class ResultServiceDialogFragment : DialogFragment() {

    private val args: ReadoutEditDialogFragmentArgs by navArgs()
    private lateinit var selectedRaceViewModel: SelectedRaceViewModel
    private val dataProcessor = DataProcessor.get()

    private lateinit var enableSwitch: SwitchMaterial
    private lateinit var typePicker: MaterialAutoCompleteTextView
    private lateinit var urlPickerLayout: TextInputLayout
    private lateinit var urlPicker: TextInputEditText

    private lateinit var okButton: Button
    private lateinit var cancelButton: Button

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
        urlPickerLayout = view.findViewById(R.id.results_service_dialog_url_layout)
        urlPicker = view.findViewById(R.id.results_service_dialog_url)
        okButton = view.findViewById(R.id.result_service_dialog_ok)
        cancelButton = view.findViewById(R.id.readout_dialog_cancel)

        populateFields()
        setButtons()
    }

    private fun populateFields() {
        dialog?.setTitle(R.string.results_service)

    }

    private fun setButtons() {
        okButton.setOnClickListener {
            if (validateFields()) {
                val serviceType =
                    dataProcessor.resultServiceTypeFromString(typePicker.text.toString())
                val url = urlPicker.text.toString()
                val enabled = enableSwitch.isChecked

//                val service = dataProcessor.createResultService(
//                    args.readoutId,
//                    serviceType,
//                    selectedRaceViewModel
//                )
            }
        }

        cancelButton.setOnClickListener {
            dialog?.cancel()
        }
    }

    private fun validateFields(): Boolean {
        var valid = true


        return valid
    }
}