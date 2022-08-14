package io.thedatapirates.cashplan.domains.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.activities.AppMainActivity
import io.thedatapirates.cashplan.activities.LoginActivity
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*

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
class HomeFragment : Fragment() {

    private lateinit var homeContext: Context
    private val customerService = CustomerServiceLocator.getCustomerService()
    private var toast: Toast? = null

    /**
     * Runs listener's when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // coroutine to run async method on separate thread
        GlobalScope.launch(Dispatchers.IO) {
            val customer = getCustomerInformation(view)

            // if user exist show welcome text
            if (customer != null) {
                withContext(Dispatchers.Main) {
                    view.tvHomeText.text =
                        getString(R.string.home_welcome, customer.firstName, customer.lastName)
                }
            } else {
                // reroutes to login page and displays error message
                withContext(Dispatchers.Main) {
                    val intent = Intent(homeContext, LoginActivity::class.java)
                    startActivity(intent)

                    toast?.cancel()

                    toast = CustomToast.createCustomToast(
                        "Sorry there was an issue with the server. Please try again",
                        view, homeContext
                    )

                    toast?.show()
                }
            }
        }

        view.tvHomeText.setOnClickListener {
        }

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContext = context
    }

    /**
     * Makes call to api to retrieve customer information
     */
    private suspend fun getCustomerInformation(view: View): CustomerResponse? {
        val customer: CustomerResponse?

        val sharedPreferences = homeContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
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