package br.edu.utfpr.marvas.greenbenchmark.ui.account

import android.annotation.SuppressLint
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
import br.edu.utfpr.marvas.greenbenchmark.data.model.Account
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var _binding: FragmentAccountBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountViewModel =
            ViewModelProvider(this, AccountViewModelFactory())[AccountViewModel::class.java]

        val firstNameEditText = binding.firstName
        val lastNameEditText = binding.lastName
        val emailEditText = binding.email
        val phoneNumberEditText = binding.phoneNumber
        val phoneCountryCodeEditText = binding.phoneCountryCode
        val accountActiveSwitch = binding.accountActive
        val notificationCheckBox = binding.notification
        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val saveAccountButton = binding.saveAccount
        val loadingProgressBar = binding.loading

        val doSave: () -> Unit = {
            accountViewModel.save(
                Account(
                    firstName = firstNameEditText.text.toString(),
                    lastName = lastNameEditText.text.toString(),
                    email = emailEditText.text.toString(),
                    phoneNumber = phoneNumberEditText.text.toString(),
                    phoneCountryCode = phoneCountryCodeEditText.text.toString(),
                    active = accountActiveSwitch.isChecked,
                    notification = notificationCheckBox.isChecked,
                    username = usernameEditText.text.toString(),
                    password = passwordEditText.text.toString()
                )
            )
        }

        accountViewModel.accountFormState.observe(viewLifecycleOwner,
            Observer { accountFormState ->
                if (accountFormState == null) {
                    return@Observer
                }
                saveAccountButton.isEnabled = accountFormState.isDataValid
                accountFormState.firstNameError?.let {
                    firstNameEditText.error = getString(it)
                }
                accountFormState.emailError?.let {
                    emailEditText.error = getString(it)
                }
                accountFormState.phoneNumberError?.let {
                    phoneNumberEditText.error = getString(it)
                }
                accountFormState.phoneCountryCodeError?.let {
                    phoneCountryCodeEditText.error = getString(it)
                }
                accountFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                accountFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        accountViewModel.accountResult.observe(viewLifecycleOwner,
            Observer { accountResult ->
                accountResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                accountResult.error?.let {
                    showAccountCreateFailed(it)
                }
                accountResult.success?.let {
                    updateUiWithAccount(it)
                }
                println("Account Executed")
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                accountViewModel.accountDataChanged(
                    firstNameEditText.text.toString(),
                    emailEditText.text.toString(),
                    phoneNumberEditText.text.toString(),
                    phoneCountryCodeEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        firstNameEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        phoneNumberEditText.addTextChangedListener(afterTextChangedListener)
        phoneCountryCodeEditText.addTextChangedListener(afterTextChangedListener)
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)

        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSave()
            }
            false
        }

        saveAccountButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            doSave()
        }

        saveAccountButton.performClick()
    }

    private fun updateUiWithAccount(model: AccountCreatedView) {
        val welcome = "Account created for " + model.displayName
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_AccountFragment_to_StartFragment)
    }

    private fun showAccountCreateFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_AccountFragment_to_StartFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}