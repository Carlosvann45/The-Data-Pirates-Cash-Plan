package io.thedatapirates.cashplan.domains.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.services.login.LoginService
import io.thedatapirates.cashplan.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject login service into login fragment
 */
object ForgotPasswordServiceLocator {
    fun getLoginService(): LoginService = LoginService.create()
}

/**
 * A simple [Fragment] subclass.
 */
@DelicateCoroutinesApi
class ForgotPasswordFragment : Fragment() {

    private lateinit var forgotPasswordContext: Context
    private val loginService = ForgotPasswordServiceLocator.getLoginService()
    private val emailRegex = "^(.+)@(.+)\\.(.+)\$".toRegex()
    private var toast: Toast? = null
    private var error = ""

    /**
     * Adds listeners and to run code when fragment is loaded
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        view.btnForgotPasswordContinue.setOnClickListener {
            val email = view.etEmailField.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                val emailSent = async { sendEmail(email) }

                withContext(Dispatchers.Main) {
                    if (emailSent.await()) {
                        Navigation.findNavController(view)
                            .navigate(R.id.navigateToPasswordConfirmation)
                    } else {
                        toast?.cancel()

                        toast = CustomToast.createCustomToast(error, view, forgotPasswordContext)

                        toast?.show()
                    }
                }
            }
        }

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        forgotPasswordContext = context
    }

    /**
     * Sends an email if email provided is valid
     */
    private suspend fun sendEmail(email: String): Boolean {
        var emailSent = false

        if (emailRegex.matches(email)) {
            try {
                loginService.sendCustomerForgotPasswordEmail(email)

                emailSent = true
            } catch (e: Exception) {
                println("Error: ${e.message}")
                error = " Sorry there was an error with the server. Try again later."
            }
        } else error = "Must provide an email that follows proper format: example@test.com"

        return emailSent
    }
}