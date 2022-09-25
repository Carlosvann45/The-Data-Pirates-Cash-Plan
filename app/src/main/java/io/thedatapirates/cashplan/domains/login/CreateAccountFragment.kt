package io.thedatapirates.cashplan.domains.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.createAccount.CreateAccountRequest
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import kotlinx.android.synthetic.main.fragment_create_account.view.*
import kotlinx.coroutines.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.thedatapirates.cashplan.utils.AndroidUtils

/**
 * Service locator to inject customer service into login fragment
 */
object CreateAccountServiceLocator {
    fun getCustomerService(): CustomerService = CustomerService.create()
}

@DelicateCoroutinesApi
class CreateAccountFragment : Fragment() {
    private val customerService = CreateAccountServiceLocator.getCustomerService()
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+[\\.][a-z]+"
    private lateinit var createAccountContext: Context
    private var toast: Toast? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_account, container, false)
        view.btnCreateAccount.setOnClickListener {
            val firstName = view.etFirstNameField.text.toString()
            val lastName = view.etLastNameField.text.toString()
            val email = view.etEmailAddressField.text.toString()
            val initialPasswordEntry = view.etPasswordEntryField.text.toString()
            val reenterPasswordEntry = view.etPasswordReentryField.text.toString()
            val customerInformation =
                CreateAccountRequest(firstName, lastName, email, initialPasswordEntry)

            if (email.matches(emailPattern.toRegex())) {
                toast?.cancel()

                toast = AndroidUtils.createCustomToast("Valid email address", view, createAccountContext)

                toast?.show()
            } else {
                toast?.cancel()

                toast = AndroidUtils.createCustomToast("Invalid email address",view, createAccountContext)

                toast?.show()
            }
            if (initialPasswordEntry.length <= 8 || initialPasswordEntry.length >= 20) {
                // Toast here - password must be between 8 and 20 characters in length
                toast?.cancel()

                toast = AndroidUtils.createCustomToast("Password must be between 8 and 20 characters", view, createAccountContext)

                toast?.show()
            }
            if (initialPasswordEntry != reenterPasswordEntry) {
                // Toast here - passwords do not match
                toast?.cancel()

                toast = AndroidUtils.createCustomToast("Passwords do not match", view, createAccountContext)

                toast?.show()
            }

            GlobalScope.launch(Dispatchers.IO) {
                if (processCreateAccount(customerInformation)) {
                    withContext(Dispatchers.Main) {
                        Navigation.findNavController(view)
                            .navigate(R.id.navigateTologinFragmentFromCreateAccount)
                    }
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createAccountContext = context
    }

    /**
     * Makes request to api to process create account request from user input
     */
    private suspend fun processCreateAccount(view: CreateAccountRequest): Boolean {
        var isLoggedIn = false


        try {
            customerService.createCustomer(view)
            isLoggedIn = true
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return isLoggedIn
    }
}