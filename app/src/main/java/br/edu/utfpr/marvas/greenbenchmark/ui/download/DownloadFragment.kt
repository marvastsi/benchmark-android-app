package br.edu.utfpr.marvas.greenbenchmark.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentDownloadBinding

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadFragment : Fragment() {
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

        val downloadButton = binding.executeDownload
        val loadingProgressBar = binding.loading

        downloadButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            showMessage()
        }

        downloadButton.performClick()
    }

    private fun showMessage() {
        println("Download Executed")
        Toast.makeText(requireContext(), "Download Executed", Toast.LENGTH_LONG).show()
//        Thread.sleep(Constants.DELAY_MS_MEDIUM)
        findNavController().navigate(R.id.action_DownloadFragment_to_StartFragment)
    }
}