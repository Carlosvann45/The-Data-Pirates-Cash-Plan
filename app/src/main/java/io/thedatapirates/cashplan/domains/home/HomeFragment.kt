package io.thedatapirates.cashplan.domains.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.dtos.expense.Withdrawal
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
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
    private lateinit var progressOverlay: View
    private lateinit var withdrawals: MutableList<Withdrawal>
    private val customerService = CustomerServiceLocator.getCustomerService()

    /**
     * Runs listener's when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        GlobalScope.launch(Dispatchers.IO) {
            getCustomerInformation(view)

            withContext(Dispatchers.Main) {
                initializeMonthlyTracker(view)

                AndroidUtils.animateView(progressOverlay, View.GONE, 0f, 200L)
            }
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

        withdrawals = customer?.withdrawals ?: mutableListOf()

        val editPreferences = sharedPreferences.edit()

        // actually saves user info
        editPreferences.apply {
            putString("firstName", customer?.firstName ?: "")
            putString("lastName", customer?.lastName ?: "")
            apply()
        }

        return customer
    }

    private fun initializeMonthlyTracker(view: View) {

    }

}