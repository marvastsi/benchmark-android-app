package br.edu.utfpr.marvas.greenbenchmark.ui.download

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentDownloadBinding

class DownloadFragment : Fragment(), TextWatcher {
    private lateinit var configRepository: ConfigRepository
    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var fileNameEditText: EditText
    private lateinit var downloadButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var config: Config
    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(
            ConfigStorage.TEST_CONFIG,
            Context.MODE_PRIVATE
        )
        val configStorage = ConfigStorage(sharedPreferences)
        configRepository = ConfigRepository(configStorage)
        config = configRepository.getConfig()
        downloadViewModel = ViewModelProvider(
            this,
            DownloadViewModelFactory(requireContext(), config)
        )[DownloadViewModel::class.java]

        fileNameEditText = binding.fileName
        downloadButton = binding.executeDownload
        loadingProgressBar = binding.loading

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadViewModel.downloadFormState.observe(
            viewLifecycleOwner,
            createFormStateObserver()
        )

        downloadViewModel.downloadResult.observe(
            viewLifecycleOwner,
            createResultObserver()
        )

        fileNameEditText.addTextChangedListener(this)
        downloadButton.setOnClickListener {
            executeDownload()
        }

        fileNameEditText.setText(config.downloadUri)
        downloadButton.performClick()
    }

    private fun executeDownload() {
        loadingProgressBar.visibility = View.VISIBLE
        val fileName: String = fileNameEditText.text.toString()
        downloadViewModel.download(fileName)
    }

    private fun createFormStateObserver(): Observer<in DownloadFormState> {
        return Observer { formState ->
            if (formState == null) {
                return@Observer
            }
            downloadButton.isEnabled = formState.isDataValid
            formState.fileNameError?.let {
                fileNameEditText.error = getString(it)
            }
        }
    }

    private fun createResultObserver(): Observer<in DownloadFileResult> {
        return Observer { result ->
            result ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            result.error?.let {
                showDownloadFailed(it)
            }
            result.success?.let {
                updateUiWithFile(it)
            }
            println("Download Executed")
        }
    }

    private fun updateUiWithFile(model: DownloadFileView) {
        Toast.makeText(
            requireContext(),
            "Download Executed: ${model.fileName()}",
            Toast.LENGTH_LONG
        ).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_DownloadFragment_to_StartFragment)
    }

    private fun showDownloadFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_DownloadFragment_to_StartFragment)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        downloadViewModel.downloadDataChanged(
            fileNameEditText.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}