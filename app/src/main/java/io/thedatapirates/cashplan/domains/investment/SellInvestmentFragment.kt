package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentRequest
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_sell_investment.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject customer service into investment fragment
 */
object SellInvestmentServiceLocator {
    fun getInvestmentService(): InvestmentService = InvestmentService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [SellInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class SellInvestmentFragment : Fragment() {

    private lateinit var sellInvestmentContext: Context
    private lateinit var totalInvestments: MutableList<TotalInvestment>
    private val investmentService = SellInvestmentServiceLocator.getInvestmentService()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sell_investment, container, false)
        val listJson = arguments?.getString("stockList")

        // gets all the json sent from the navigation
        totalInvestments =
            if (listJson != null)
                Gson().fromJson(
                    listJson,
                    object : TypeToken<MutableList<TotalInvestment>>() {}.type
                )
            else mutableListOf()


        // created a list of options for the custom picker
        val options = mutableListOf<String>()
        options.add("Choose Stock")

        totalInvestments.forEach { if (it.name != "") options.add(it.name) }

        // sets up the custom picker for scrolling through options
        view.npCustomPicker.minValue = 0
        view.npCustomPicker.maxValue = (options.size - 1)
        view.npCustomPicker.displayedValues = options.toTypedArray()
        view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
            view.etStockToSell.setText(options[newVal], TextView.BufferType.EDITABLE)

            val amount: Double = totalInvestments.find {
                it.name == view.etStockToSell.text.toString()
            }?.totalAmount ?: 0.01

            view.tvTotalAmountAvailable.text =
                "You currently have $${String.format("%,.2f", amount)} available"
        }

        // when dropdown button is selected makes sure picker is visible
        view.ivOpenSelectStockPicker.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        // when done button is selected makes sure picker is closed
        view.tvCustomPickerDone.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.GONE
        }

        // if back button is clicked navigates back to investment overview
        view.ivSellInvestmentBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToInvestmentFragmentFromSell)
        }

        // handles calculating sell transaction for selected stock
        view.btnSellStock.setOnClickListener {
            val amountToSell = view.etAmountToSell.text.toString()
            val totalAmount: Double =
                totalInvestments.find { it.name == view.etStockToSell.text.toString() }?.totalAmount
                    ?: 0.00

            // if selected sell amount is meets criteria make request for sell transaction
            // otherwise it will throw toast
            if (amountToSell != "" && amountToSell.toDouble() > 0.00 && amountToSell.toDouble() < totalAmount) {
                val investmentToSell =
                    totalInvestments.find { it.name == view.etStockToSell.text.toString() }

                GlobalScope.launch(Dispatchers.IO) {

                    val stockWasSold: Boolean

                    if (investmentToSell != null) {
                        // if sell amount is the total amount available
                        if (amountToSell.toDouble() == totalAmount) {
                            // calculates sell transaction
                            val newInvestment = InvestmentRequest(
                                investmentToSell.name,
                                investmentToSell.sector,
                                investmentToSell.shares,
                                (investmentToSell.totalAmount / investmentToSell.shares) * -1
                            )

                            // send sell transaction to api
                            stockWasSold = createInvestment(newInvestment)

                            // else if transaction it less then total amount
                        } else {
                            // calculates sell transaction
                            val amount = amountToSell.toDouble() / investmentToSell.buyPrice
                            val newInvestment = InvestmentRequest(
                                investmentToSell.name,
                                investmentToSell.sector,
                                amount,
                                (amountToSell.toDouble() / amount) * -1
                            )

                            // send sell transaction to api
                            stockWasSold = createInvestment(newInvestment)

                        }

                        withContext(Dispatchers.Main) {
                            // if transaction was completed navigates to investment overview page
                            if (stockWasSold) {
                                Navigation.findNavController(view)
                                    .navigate(R.id.navigateToInvestmentFragmentFromSell)
                                // else if transaction failed throws error toast
                            } else {
                                // throw error toast
                                toast?.cancel()

                                toast = AndroidUtils.createCustomToast(
                                    "There was an issue with the server. Please try again later.",
                                    view,
                                    sellInvestmentContext
                                )

                                toast?.show()
                            }

                        }
                    }
                }
                // if validation doesn't pass throws error toast
            } else {
                // throw error toast
                toast?.cancel()

                toast = AndroidUtils.createCustomToast(
                    "Amount must be greater the 0.00 and less then total stock amount available.",
                    view,
                    sellInvestmentContext
                )

                toast?.show()
            }
        }

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sellInvestmentContext = context
    }

    /**
     * Makes call to api to process sell transaction
     */
    private suspend fun createInvestment(investment: InvestmentRequest): Boolean {
        var wasSold = false
        val sharedPreferences =
            sellInvestmentContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            investmentService.createInvestment(investment, accessToken)
            wasSold = true
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return wasSold
    }

}