package br.edu.utfpr.marvas.greenbenchmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.commons.Config
import br.edu.utfpr.marvas.greenbenchmark.commons.TestExecution
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentStartBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startButton = binding.buttonStart
        val startTextview = binding.textviewStart
        val testLoad = getString(R.string.test_load).toInt()

        val testExecution = TestExecution.getInstance(Config(testLoad))

        if (testExecution.hasNext()) {
            val route = testExecution.next()
            startButton.setOnClickListener {
                val text = getString(R.string.test_execution_running)
                startTextview.setText(text)
                findNavController().navigate(route)
            }
        } else {
            val text = getString(R.string.test_execution_finished)
            startTextview.setText(text)
            startButton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    text,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (testExecution.isRunning()) {
            startButton.performClick()
        } else {
            testExecution.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}