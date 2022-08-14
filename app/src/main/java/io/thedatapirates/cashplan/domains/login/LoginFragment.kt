package io.thedatapirates.cashplan.domains.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.activities.AppMainActivity
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.dtos.login.LoginRequest
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.data.services.login.LoginService
import io.thedatapirates.cashplan.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject login service into login fragment
 */
object LoginServiceLocator {
    fun getLoginService(): LoginService = LoginService.create()
}

/**
 * Service locator to inject customer service into login fragment
 */
object CustomerServiceLocator {
    fun getCustomerService(): CustomerService = CustomerService.create()
}

/**
 * A simple [Fragment] subclass.
 */
@DelicateCoroutinesApi
class LoginFragment : Fragment() {

    private lateinit var loginContext: Context
    private val loginService = LoginServiceLocator.getLoginService()
    private val customerService = CustomerServiceLocator.getCustomerService()
    private var toast: Toast? = null
    private var error = ""

    /**
     * Runs listener's when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.btnLogin.setOnClickListener {
            // coroutine to run async code in a separate thread
            GlobalScope.launch(Dispatchers.IO) {
                val isLoggedIn = processLogin(view)

                // whenever changing fragments/activities you have to switch to the main thread
                withContext(Dispatchers.Main) {
                    if (!isLoggedIn) {
                        toast?.cancel()

                        toast = CustomToast.createCustomToast(error, view, loginContext)

                        toast?.show()
                    }
                }

                if (isLoggedIn) {

                    val customer = getCustomerInformation(view)

                    // if user exist show welcome text
                    if (customer != null) {
                        withContext(Dispatchers.Main) {
                            val sharedPreferences =
                                loginContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

                            sharedPreferences.edit().apply {
                                putString("customerFirstName", customer.firstName)
                                putString("customerLastName", customer.lastName)
                                apply()
                            }

                            val intent = Intent(loginContext, AppMainActivity::class.java)

                            // clears fields
                            view.etUsernameField.text.clear()
                            view.etPasswordField.text.clear()

                            // moves to the main activity once logged in
                            startActivity(intent)
                        }
                    } else {
                        // reroutes to login page and displays error message
                        withContext(Dispatchers.Main) {

                            toast?.cancel()

                            toast = CustomToast.createCustomToast(
                                "Sorry there was an issue with the server. Please try again",
                                view, loginContext
                            )

                            toast?.show()
                        }
                    }
                }
            }
        }

        view.tvForgotPassword.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToForgotPasswordFragment)
        }

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginContext = context
    }

    /**
     * Makes request to api to process login request from user input
     */
    private suspend fun processLogin(view: View): Boolean {
        var isLoggedIn = false

        val loginRequest = LoginRequest(
            view.etUsernameField.text.toString(),
            view.etPasswordField.text.toString()
        )

        try {
            val loginResponse = loginService.loginCustomer(loginRequest)

            // adds shared preferences to store tokens and user email
            val sharedPreferences =
                loginContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
            val editPreferences = sharedPreferences.edit()

            // actually saves user info
            editPreferences.apply {
                putString("accessToken", loginResponse?.accessToken)
                putString("refresherToken", loginResponse?.refresherToken)
                putString("userEmail", loginRequest.username)
                apply()
            }

            isLoggedIn = true
        } catch (e: ClientRequestException) {
            println("Error: ${e.response.status.description}")
            error = "Your username or password is invalid."
        } catch (e: Exception) {
            println("Error: ${e.message}")
            error = " Sorry there was an error with the server. Try again later."
        }

        return isLoggedIn
    }


    /**
     * Makes call to api to retrieve customer information
     */
    private suspend fun getCustomerInformation(view: View): CustomerResponse? {
        val customer: CustomerResponse?

        val sharedPreferences = loginContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")
        val email = sharedPreferences.getString("userEmail", "")

        customer = try {
            customerService.getCustomerInformation(email, accessToken)
        } catch (e: ClientRequestException) {
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }

        return customer
    }
}