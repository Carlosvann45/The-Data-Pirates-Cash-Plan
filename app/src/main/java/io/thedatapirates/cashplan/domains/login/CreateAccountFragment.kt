package io.thedatapirates.cashplan.domains.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.createAccount.CreateAccountRequest
import io.thedatapirates.cashplan.data.dtos.login.LoginRequest
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import kotlinx.android.synthetic.main.fragment_create_account.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
                Toast.makeText(createAccountContext, "Valid email address",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(createAccountContext, "Invalid email address",
                    Toast.LENGTH_SHORT).show()
            }
            if (initialPasswordEntry.length <= 8 || initialPasswordEntry.length >= 20) {
                // Toast here - password must be between 8 and 20 characters in length
                Toast.makeText(createAccountContext, "Password must be between 8 and 20 characters",
                    Toast.LENGTH_SHORT).show()
            }
            if (initialPasswordEntry != reenterPasswordEntry) {
                // Toast here - passwords do not match
                Toast.makeText(createAccountContext, "Passwords do not match",
                    Toast.LENGTH_SHORT).show()
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