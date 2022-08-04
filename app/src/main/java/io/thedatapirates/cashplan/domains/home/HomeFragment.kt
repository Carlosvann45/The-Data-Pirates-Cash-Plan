package io.thedatapirates.cashplan.domains.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.*
import java.lang.Exception

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        GlobalScope.launch(Dispatchers.IO) {
            val customer = getCustomerInformation(view)

            if (customer != null) {
                withContext(Dispatchers.Main) {
                    view.tvHomeText.text = "Welcome ${customer.firstName} to your home page!"
                }
            }
        }

            view.tvHomeText.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.navigateToLoginFragment)
            }

            return view
        }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContext = context
    }

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