package br.edu.utfpr.marvas.greenbenchmark.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Account
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentAccountBinding

@SuppressLint("UseSwitchCompatOrMaterialCode")
class AccountFragment : Fragment(), TextWatcher, AdapterView.OnItemSelectedListener {
    private lateinit var configRepository: ConfigRepository
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var phoneCountryCodeSpinner: Spinner
    private lateinit var accountActiveSwitch: Switch
    private lateinit var notificationCheckBox: CheckBox
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveAccountButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var config: Config
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(
            ConfigStorage.TEST_CONFIG,
            Context.MODE_PRIVATE
        )
        val configStorage = ConfigStorage(sharedPreferences)
        configRepository = ConfigRepository(configStorage)
        config = configRepository.getConfig()
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(requireContext(), config)
        )[AccountViewModel::class.java]

        firstNameEditText = binding.firstName
        lastNameEditText = binding.lastName
        emailEditText = binding.email
        phoneNumberEditText = binding.phoneNumber
        phoneCountryCodeSpinner = binding.phoneCountryCode
        accountActiveSwitch = binding.accountActive
        notificationCheckBox = binding.notification
        usernameEditText = binding.username
        passwordEditText = binding.password
        saveAccountButton = binding.saveAccount
        loadingProgressBar = binding.loading

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountViewModel.accountFormState.observe(
            viewLifecycleOwner,
            createFormStateObserver()
        )

        accountViewModel.accountResult.observe(
            viewLifecycleOwner,
            createResultObserver()
        )

        createArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            R.array.country_codes
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            phoneCountryCodeSpinner.adapter = adapter
        }

        firstNameEditText.addTextChangedListener(this)
        emailEditText.addTextChangedListener(this)
        phoneNumberEditText.addTextChangedListener(this)
        phoneCountryCodeSpinner.onItemSelectedListener = this
        usernameEditText.addTextChangedListener(this)
        passwordEditText.addTextChangedListener(this)

        saveAccountButton.setOnClickListener {
            doSave()
        }

        fillValues()
        saveAccountButton.performClick()
    }

    private fun fillValues() {
        firstNameEditText.setText(R.string.first_name)
        lastNameEditText.setText(R.string.last_name)
        emailEditText.setText(R.string.email)
        phoneNumberEditText.setText(R.string.phone_number)
        phoneCountryCodeSpinner.setSelection(1)
        accountActiveSwitch.isChecked = resources.getBoolean(R.bool.active)
        notificationCheckBox.isChecked = resources.getBoolean(R.bool.notification)
        usernameEditText.setText(R.string.username)
        passwordEditText.setText(R.string.password)
    }

    private fun doValidate() {
        loadingProgressBar.visibility = View.VISIBLE
        accountViewModel.accountDataChanged(
            firstNameEditText.text.toString(),
            emailEditText.text.toString(),
            phoneNumberEditText.text.toString(),
            phoneCountryCodeSpinner.selectedItem.toString(),
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        )
    }

    private fun doSave() {
        loadingProgressBar.visibility = View.VISIBLE
        accountViewModel.save(
            Account(
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                email = emailEditText.text.toString(),
                phoneNumber = phoneNumberEditText.text.toString(),
                phoneCountryCode = phoneCountryCodeSpinner.selectedItem.toString(),
                active = accountActiveSwitch.isChecked,
                notification = notificationCheckBox.isChecked,
                username = usernameEditText.text.toString(),
                password = passwordEditText.text.toString()
            )
        )
    }

    private fun createFormStateObserver(): Observer<in AccountFormState> {
        return Observer { accountFormState ->
            accountFormState ?: return@Observer
            loadingProgressBar.visibility = View.GONE
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
                val errorText = phoneCountryCodeSpinner.selectedView as TextView
                errorText.error = getString(it)
            }
            accountFormState.usernameError?.let {
                usernameEditText.error = getString(it)
            }
            accountFormState.passwordError?.let {
                passwordEditText.error = getString(it)
            }
        }
    }

    private fun createResultObserver(): Observer<in CreateAccountResult> {
        return Observer { accountResult ->
            accountResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            accountResult.error?.let {
                showAccountCreateFailed(it)
            }
            accountResult.success?.let {
                updateUiWithAccount(it)
            }
            println("Account Executed")
        }
    }

    private fun updateUiWithAccount(model: AccountCreatedView) {
        val welcome = "Account created with id: " + model.accountId
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_SHORT).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_AccountFragment_to_StartFragment)
    }

    private fun showAccountCreateFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_AccountFragment_to_StartFragment)
    }

    private fun createArrayAdapter(
        context: Context,
        @LayoutRes textViewResId: Int,
        @ArrayRes textArrayResId: Int
    ) = object : ArrayAdapter<String>(
        context,
        textViewResId,
        resources.getStringArray(textArrayResId)
    ) {
        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view as TextView
            if (position == 0) {
                textView.setTextColor(Color.GRAY)
            } else {
                textView.setTextColor(Color.BLACK)
            }
            return view
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) = run { doValidate() }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
        run { doValidate() }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}