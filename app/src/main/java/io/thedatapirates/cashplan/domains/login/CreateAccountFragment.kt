package io.thedatapirates.cashplan.domains.login

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

/**
 * Service locator to inject customer service into login fragment
 */
object CreateAccountServiceLocator {
    fun getCustomerService(): CustomerService = CustomerService.create()
}

@DelicateCoroutinesApi
class CreateAccountFragment : Fragment() {
    private val customerService = CreateAccountServiceLocator.getCustomerService()
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