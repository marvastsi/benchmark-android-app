package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentUploadBinding
import java.net.URLDecoder

class UploadFragment : Fragment(), TextWatcher {
    private lateinit var configRepository: ConfigRepository
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var fileNameEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var config: Config
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(
            ConfigStorage.TEST_CONFIG,
            Context.MODE_PRIVATE
        )
        val configStorage = ConfigStorage(sharedPreferences)
        configRepository = ConfigRepository(configStorage)
        config = configRepository.getConfig()
        uploadViewModel = ViewModelProvider(
            this,
            UploadViewModelFactory(requireContext(), config)
        )[UploadViewModel::class.java]

        fileNameEditText = binding.fileUpload
        uploadButton = binding.executeUpload
        loadingProgressBar = binding.loading
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadViewModel.uploadFormState.observe(
            viewLifecycleOwner,
            createFormStateObserver()
        )

        uploadViewModel.uploadResult.observe(
            viewLifecycleOwner,
            createResultObserver()
        )

        fileNameEditText.addTextChangedListener(this)

        uploadButton.setOnClickListener {
            executeUpload()
        }

        fileNameEditText.setText(config.uploadUri)

        uploadButton.performClick()
    }

    private fun executeUpload() {
        loadingProgressBar.visibility = View.VISIBLE
        val filePath: String = fileNameEditText.text.toString()
        uploadViewModel.upload(filePath)
    }

    private fun createFormStateObserver(): Observer<in UploadFormState> {
        return Observer { formState ->
            if (formState == null) {
                return@Observer
            }
            uploadButton.isEnabled = formState.isDataValid
            formState.fileNameError?.let {
                fileNameEditText.error = getString(it)
            }
        }
    }

    private fun createResultObserver(): Observer<in UploadFileResult> {
        return Observer { uploadResult ->
            uploadResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            uploadResult.error?.let {
                showUploadFailed(it)
            }
            uploadResult.success?.let {
                updateUiWithFile(it)
            }
            println("Upload Executed")
        }
    }

    private fun updateUiWithFile(model: UploadFileView) {
        Toast.makeText(
            requireContext(),
            "Upload Executed: ${model.fileName()}",
            Toast.LENGTH_LONG
        ).show()
        Thread.sleep(Constants.DELAY_MS_MEDIUM)
        findNavController().navigate(R.id.action_UploadFragment_to_ExecutionFragment)
    }

    private fun showUploadFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_UploadFragment_to_ExecutionFragment)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        uploadViewModel.uploadDataChanged(
            fileNameEditText.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
