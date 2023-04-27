package br.edu.utfpr.marvas.greenbenchmark.ui.config

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentConfigBinding

class ConfigFragment : Fragment(), TextWatcher, AdapterView.OnItemSelectedListener {
    private lateinit var configViewModel: ConfigViewModel
    private lateinit var tvTestLoad: EditText
    private lateinit var tvMediaFile: EditText
    private lateinit var tvUploadFile: EditText
    private lateinit var tvDownloadFile: EditText
    private lateinit var tvServerUrl: EditText
    private lateinit var specificScenarioSpinner: Spinner
    private lateinit var btnSaveConfig: Button
    private lateinit var btnChooseMedia: Button
    private lateinit var btnChooseUpload: Button
    private lateinit var loadingProgressBar: ProgressBar
    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaUri: String
    private lateinit var uploadUri: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        configViewModel = ViewModelProvider(
            this,
            ConfigViewModelFactory(requireContext())
        )[ConfigViewModel::class.java]

        tvTestLoad = binding.testLoad
        tvMediaFile = binding.mediaUri
        tvUploadFile = binding.uploadUri
        tvDownloadFile = binding.downloadUri
        tvServerUrl = binding.serverUrl
        specificScenarioSpinner = binding.specificScenario
        btnSaveConfig = binding.saveConfig
        btnChooseMedia = binding.chooseMedia
        btnChooseUpload = binding.chooseUpload
        loadingProgressBar = binding.loading

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configViewModel.configFormState.observe(
            viewLifecycleOwner,
            createFormStateObserver()
        )

        configViewModel.configResult.observe(
            viewLifecycleOwner,
            createResultObserver()
        )

        createArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            R.array.scenarios
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            specificScenarioSpinner.adapter = adapter
        }

        tvTestLoad.addTextChangedListener(this)
        tvMediaFile.addTextChangedListener(this)
        tvUploadFile.addTextChangedListener(this)
        tvDownloadFile.addTextChangedListener(this)
        tvServerUrl.addTextChangedListener(this)
        specificScenarioSpinner.onItemSelectedListener = this

        btnChooseMedia.setOnClickListener {
            getMediaContent.launch("video/*")
        }
        btnChooseUpload.setOnClickListener {
            getUploadContent.launch("*/*")
        }

        btnSaveConfig.setOnClickListener {
            doSave()
        }
    }

    private fun doSave() {
        loadingProgressBar.visibility = View.VISIBLE
        val config = Config(
            tvTestLoad.text.toString().toInt(),
            mediaUri,
            uploadUri,
            tvDownloadFile.text.toString(),
            tvServerUrl.text.toString(),
            specificScenarioSpinner.selectedItemPosition
        )
        configViewModel.saveConfig(config)
    }

    private fun createFormStateObserver(): Observer<in ConfigFormState> {
        return Observer { configFormState ->
            if (configFormState == null) {
                return@Observer
            }
            btnSaveConfig.isEnabled = configFormState.isDataValid
            configFormState.testLoadError?.let {
                tvTestLoad.error = getString(it)
            }
            configFormState.mediaUriError?.let {
                tvMediaFile.error = getString(it)
            }
            configFormState.uploadUriError?.let {
                tvUploadFile.error = getString(it)
            }
            configFormState.downloadUriError?.let {
                tvDownloadFile.error = getString(it)
            }
            configFormState.serverUrlError?.let {
                tvServerUrl.error = getString(it)
            }
        }
    }

    private fun createResultObserver(): Observer<in ConfigResult> {
        return Observer { configResult ->
            configResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            configResult.error?.let {
                showConfigFailed(it)
            }
            configResult.success?.let {
                updateUiWithConfig(it)
            }
        }
    }

    private fun updateUiWithConfig(model: ConfigView) {
        println("Test Configuration Loaded")
        Toast.makeText(requireContext(), "Config loaded: ${model.serverUrl}", Toast.LENGTH_SHORT)
            .show()
        findNavController().navigate(R.id.action_ConfigFragment_to_ExecutionFragment)
    }

    private fun showConfigFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    private fun createArrayAdapter(
        context: Context,
        @LayoutRes textViewResId: Int,
        @ArrayRes textArrayResId: Int
    ) = object : ArrayAdapter<String>(
        context,
        textViewResId,
        resources.getStringArray(textArrayResId)
    ) {
        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view as TextView
            if (position == 0) {
                textView.setTextColor(Color.GRAY)
            } else {
                textView.setTextColor(Color.BLACK)
            }
            return view
        }
    }

    private val getMediaContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            mediaUri = uri.toString()
            tvMediaFile.setText(uri?.lastPathSegment)
        }

    private val getUploadContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uploadUri = uri.toString()
            tvUploadFile.setText(uri?.lastPathSegment)
        }

    private fun doValidate() {
        configViewModel.configDataChanged(
            tvTestLoad.text.toString(),
            tvMediaFile.text.toString(),
            tvUploadFile.text.toString(),
            tvDownloadFile.text.toString(),
            tvServerUrl.text.toString(),
            specificScenarioSpinner.selectedItem.toString()
        )
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        doValidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
        run { doValidate() }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
