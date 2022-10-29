package br.edu.utfpr.marvas.greenbenchmark.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaButton = binding.executeMedia
        val loadingProgressBar = binding.loading

        mediaButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            showMessage()
        }

        mediaButton.performClick()
    }

    private fun showMessage() {
        println("Media Executed")
        Toast.makeText(requireContext(), "Media Executed", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_MediaFragment_to_StartFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}