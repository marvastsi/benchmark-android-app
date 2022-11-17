package br.edu.utfpr.marvas.greenbenchmark.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.commons.TestExecution
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startButton = binding.buttonStart
        val startTextview = binding.textviewStart

        val testLoad = arguments?.getLong(Constants.LOAD_PARAM, Constants.DEFAULT_LOAD)
        val config = testLoad?.let { Config(it) } ?: Config()

        val testExecution = TestExecution.getInstance(config)

        if (testExecution.hasNext()) {
            val route = testExecution.next()
            startButton.setOnClickListener {
                if (!testExecution.isRunning()) {
                    testExecution.start()
                }
                val text = getString(R.string.test_execution_running)
                startTextview.text = text
                findNavController().navigate(route)
            }

            if (testExecution.isRunning()) startButton.performClick()
        } else {
            val text = getString(R.string.test_execution_finished)
            startTextview.text = text
            Toast.makeText(
                requireContext(),
                text,
                Toast.LENGTH_LONG
            ).show()
            startButton.text = getString(R.string.action_reconfigure)
            startButton.setOnClickListener {
                testExecution.stop()
                findNavController().navigate(R.id.action_StartFragment_to_ConfigFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}