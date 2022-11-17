package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.os.Bundle
import android.os.Environment
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
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentUploadBinding
import java.io.File

class UploadFragment : Fragment() {
    private lateinit var uploadViewModel: UploadViewModel
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadViewModel = ViewModelProvider(
            this,
            UploadViewModelFactory(requireContext())
        )[UploadViewModel::class.java]

        val fileNameEditText = binding.fileUpload
        val uploadButton = binding.executeUpload
        val loadingProgressBar = binding.loading

        uploadViewModel.uploadFormState.observe(viewLifecycleOwner,
            Observer { formState ->
                if (formState == null) {
                    return@Observer
                }
                uploadButton.isEnabled = formState.isDataValid
                formState.fileNameError?.let {
                    fileNameEditText.error = getString(it)
                }
            })

        uploadViewModel.uploadResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                result.error?.let {
                    showUploadFailed(it)
                }
                result.success?.let {
                    updateUiWithFile(it)
                }
                println("Upload Executed")
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                uploadViewModel.uploadDataChanged(
                    fileNameEditText.text.toString()
                )
            }
        }
        fileNameEditText.addTextChangedListener(afterTextChangedListener)
        fileNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val fileName: String = fileNameEditText.text.toString()
                uploadViewModel.upload(getFile(fileName))
            }
            false
        }

        uploadButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            val fileName: String = fileNameEditText.text.toString()
            uploadViewModel.upload(getFile(fileName))
        }

        fileNameEditText.setText(R.string.file_to_upload)
        uploadButton.performClick()
    }

    private fun getFile(fileName: String): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
    }

    private fun updateUiWithFile(model: UploadFileView) {
        Toast.makeText(requireContext(), "Upload Executed: ${model.fileName()}", Toast.LENGTH_LONG).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_UploadFragment_to_StartFragment)
    }

    private fun showUploadFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_UploadFragment_to_StartFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}