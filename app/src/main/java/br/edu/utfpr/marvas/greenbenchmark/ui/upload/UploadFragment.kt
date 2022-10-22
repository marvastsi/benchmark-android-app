package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentUploadBinding

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment() {

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

        val uploadButton = binding.executeUpload
        val loadingProgressBar = binding.loading

        uploadButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            showMessage()
        }

        uploadButton.performClick()
    }

    private fun showMessage() {
        println("Upload Executed")
        Toast.makeText(requireContext(), "Upload Executed", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_UploadFragment_to_StartFragment)
    }
}