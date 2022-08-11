package io.thedatapirates.cashplan.domains.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.login.LoginRequest
import io.thedatapirates.cashplan.data.services.login.LoginService
import io.thedatapirates.cashplan.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * Service locator to inject login service into login fragment
 */
object LoginServiceLocator {
    fun getLoginService(): LoginService = LoginService.create()
}

/**
 * A simple [Fragment] subclass.
 */
@DelicateCoroutinesApi
class LoginFragment : Fragment() {

    private lateinit var loginContext: Context
    private val loginService = LoginServiceLocator.getLoginService()
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

                if (isLoggedIn) {
                    // whenever changing fragments/activities you have to switch to the main thread
                    withContext(Dispatchers.Main) {
                        Navigation.findNavController(view).navigate(R.id.navigateToHomeFragment)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        toast?.cancel()

                        toast = CustomToast.createCustomToast(error, view, loginContext)

                        toast?.show()
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
            val sharedPreferences = loginContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
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
}