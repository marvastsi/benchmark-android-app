package br.edu.utfpr.marvas.greenbenchmark.ui.download

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentDownloadBinding

class DownloadFragment : Fragment() {
    private lateinit var downloadViewModel: DownloadViewModel
    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadViewModel = ViewModelProvider(
            this,
            DownloadViewModelFactory(requireContext())
        )[DownloadViewModel::class.java]

        val fileNameEditText = binding.fileName
        val downloadButton = binding.executeDownload
        val loadingProgressBar = binding.loading

        downloadViewModel.downloadFormState.observe(viewLifecycleOwner,
            Observer { formState ->
                if (formState == null) {
                    return@Observer
                }
                downloadButton.isEnabled = formState.isDataValid
                formState.fileNameError?.let {
                    fileNameEditText.error = getString(it)
                }
            })

        downloadViewModel.downloadResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                result.error?.let {
                    showDownloadFailed(it)
                }
                result.success?.let {
                    updateUiWithFile(it)
                }
                println("Download Executed")
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                downloadViewModel.downloadDataChanged(
                    fileNameEditText.text.toString()
                )
            }
        }
        fileNameEditText.addTextChangedListener(afterTextChangedListener)
        fileNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                downloadViewModel.download(fileNameEditText.text.toString())
            }
            false
        }

        downloadButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            val fileName: String = fileNameEditText.text.toString()
            downloadViewModel.download(fileName)
        }

        fileNameEditText.setText(R.string.file_to_download)
        downloadButton.performClick()
    }

    private fun updateUiWithFile(model: DownloadFileView) {
        Toast.makeText(requireContext(), "Download Executed: ${model.fileName()}", Toast.LENGTH_LONG).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_DownloadFragment_to_StartFragment)
    }

    private fun showDownloadFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_DownloadFragment_to_StartFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}