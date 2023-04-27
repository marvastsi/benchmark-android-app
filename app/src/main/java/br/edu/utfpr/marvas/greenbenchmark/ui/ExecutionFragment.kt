package br.edu.utfpr.marvas.greenbenchmark.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.TestExecution
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentExecutionBinding

class ExecutionFragment : Fragment() {
    private lateinit var configRepository: ConfigRepository
    private var _binding: FragmentExecutionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExecutionBinding.inflate(inflater, container, false)
        val configStorage = ConfigStorage(
            requireContext().getSharedPreferences(
                ConfigStorage.TEST_CONFIG,
                Context.MODE_PRIVATE
            )
        )
        configRepository = ConfigRepository(configStorage)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton = binding.buttonStart
        val startTextview = binding.textviewStart
        val config = configRepository.getConfig()

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
                findNavController().navigate(R.id.action_ExecutionFragment_to_ConfigFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}