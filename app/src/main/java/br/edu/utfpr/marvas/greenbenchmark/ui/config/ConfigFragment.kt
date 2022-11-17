package br.edu.utfpr.marvas.greenbenchmark.ui.config

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
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {
    private lateinit var configViewModel: ConfigViewModel
    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configViewModel = ViewModelProvider(
            this,
            ConfigViewModelFactory()
        )[ConfigViewModel::class.java]

        val tvTestLoad = binding.testLoad
        val btnLoadConfig = binding.loadConfig
        val loadingProgressBar = binding.loading

        configViewModel.configFormState.observe(viewLifecycleOwner,
            Observer { configFormState ->
                if (configFormState == null) {
                    return@Observer
                }
                btnLoadConfig.isEnabled = configFormState.isDataValid
                configFormState.testLoadError?.let {
                    tvTestLoad.error = getString(it)
                }
            })

        configViewModel.configResult.observe(viewLifecycleOwner,
            Observer { configResult ->
                configResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                configResult.error?.let {
                    showConfigFailed(it)
                }
                configResult.success?.let {
                    updateUiWithConfig(it)
                }
                println("Test Configuration Loaded")
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                configViewModel.configDataChanged(
                    tvTestLoad.text.toString()
                )
            }
        }
        tvTestLoad.addTextChangedListener(afterTextChangedListener)

        tvTestLoad.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                configViewModel.loadConfig(
                    tvTestLoad.text.toString()
                )
            }
            false
        }

        btnLoadConfig.setOnClickListener {
            configViewModel.loadConfig(
                tvTestLoad.text.toString()
            )
        }
    }

    private fun updateUiWithConfig(model: ConfigView) {
        val testLoad = model.testLoad
        Toast.makeText(requireContext(), "Config testLoad: $testLoad", Toast.LENGTH_SHORT).show()

        val bundle = Bundle()
        bundle.putLong(Constants.LOAD_PARAM, testLoad)

        findNavController().navigate(R.id.action_ConfigFragment_to_StartFragment, bundle)
    }

    private fun showConfigFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
