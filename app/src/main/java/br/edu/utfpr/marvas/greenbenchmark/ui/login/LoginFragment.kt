package br.edu.utfpr.marvas.greenbenchmark.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.commons.snack
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), TextWatcher {
    private lateinit var configRepository: ConfigRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var config: Config
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(
            ConfigStorage.TEST_CONFIG,
            Context.MODE_PRIVATE
        )
        val configStorage = ConfigStorage(sharedPreferences)
        configRepository = ConfigRepository(configStorage)
        config = configRepository.getConfig()
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(requireContext(), config)
        )[LoginViewModel::class.java]

        usernameEditText = binding.username
        passwordEditText = binding.password
        loginButton = binding.login
        loadingProgressBar = binding.loading
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            createFormStateObserver()
        )

        loginViewModel.loginResult.observe(
            viewLifecycleOwner,
            createResultObserver()
        )

        usernameEditText.addTextChangedListener(this)
        passwordEditText.addTextChangedListener(this)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                executeLogin()
            }
            true
        }

        loginButton.setOnClickListener {
            executeLogin()
        }

        usernameEditText.setText(R.string.username)
        passwordEditText.setText(R.string.password)
        loginButton.performClick()
    }

    private fun executeLogin() {
        loadingProgressBar.visibility = View.VISIBLE
        loginViewModel.login(
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        )
    }

    private fun createFormStateObserver(): Observer<in LoginFormState> {
        return Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            loginFormState.usernameError?.let {
                usernameEditText.error = getString(it)
            }
            loginFormState.passwordError?.let {
                passwordEditText.error = getString(it)
            }
        }
    }

    private fun createResultObserver(): Observer<in LoginResult> {
        return Observer { loginResult ->
            loginResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            loginResult.error?.let {
                showLoginFailed(it)
            }
            loginResult.success?.let {
                updateUiWithUser(it)
            }
            println("Login Executed")
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val msg = getString(R.string.welcome) + model.displayName
        requireView().snack(msg)
        Thread.sleep(Constants.DELAY_MS_SHORT)
        findNavController().navigateUp()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        requireView().snack(errorString)
        findNavController().navigateUp()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        loginViewModel.loginDataChanged(
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}