package br.edu.utfpr.marvas.greenbenchmark.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.commons.TestExecution
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentExecutionBinding
import kotlinx.coroutines.awaitAll
import java.time.LocalDateTime

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
        val startTimestampTextview = binding.textviewStartTimestamp
        val stopTimestampTextview = binding.textviewStopTimestamp
        val config = configRepository.getConfig()

        val testExecution = TestExecution.getInstance(config)

        if (testExecution.hasNext()) {
            val route = testExecution.next()
            startButton.setOnClickListener {
                if (!testExecution.isRunning()) {
                    startTimestampText = getStartTimestampText()
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
            startButton.text = getString(R.string.action_reconfigure)
            startButton.visibility = View.INVISIBLE

            startTimestampTextview.text = startTimestampText
            stopTimestampTextview.text = getStopTimestampText()

            startTimestampTextview.visibility = View.VISIBLE
            stopTimestampTextview.visibility = View.VISIBLE
        }
    }

    private fun getStartTimestampText(): String {
        return "Start: ${System.currentTimeMillis()}"
    }

    private fun getStopTimestampText(): String {
        return "Stop: ${System.currentTimeMillis()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var startTimestampText = "Start: "
    }
}