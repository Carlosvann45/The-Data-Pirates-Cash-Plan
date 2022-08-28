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

        totalInvestments =
            if (listJson != null)
                Gson().fromJson(
                    listJson,
                    object : TypeToken<MutableList<TotalInvestment>>() {}.type
                )
            else mutableListOf()


        val options = mutableListOf<String>()
        options.add("Choose Stock")

        totalInvestments.forEach { if (it.name != "") options.add(it.name) }

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

        view.ivOpenSelectStockPicker.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        view.tvCustomPickerDone.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.GONE
        }

        view.ivSellInvestmentBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToInvestmentFragmentFromSell)
        }

        view.btnSellStock.setOnClickListener {
            val amountToSell = view.etAmountToSell.text.toString()
            val totalAmount: Double =
                totalInvestments.find { it.name == view.etStockToSell.text.toString() }?.totalAmount
                    ?: 0.00

            if (amountToSell != "" && amountToSell.toDouble() > 0.00 && amountToSell.toDouble() < totalAmount) {
                val investmentToSell =
                    totalInvestments.find { it.name == view.etStockToSell.text.toString() }

                GlobalScope.launch(Dispatchers.IO) {

                    val stockWasSold: Boolean

                    if (investmentToSell != null) {
                        if (amountToSell.toDouble() == totalAmount) {
                            val newInvestment = InvestmentRequest(
                                investmentToSell.name,
                                investmentToSell.sector,
                                investmentToSell.shares,
                                (investmentToSell.totalAmount / investmentToSell.shares) * -1
                            )

                            // send request to api
                            stockWasSold = createInvestment(newInvestment)

                        } else {
                            val amount = amountToSell.toDouble() / investmentToSell.buyPrice
                            val newInvestment = InvestmentRequest(
                                investmentToSell.name,
                                investmentToSell.sector,
                                amount,
                                (amountToSell.toDouble() / amount) * -1
                            )

                            // send request to api
                            stockWasSold = createInvestment(newInvestment)

                        }

                        withContext(Dispatchers.Main) {
                            if (stockWasSold) {
                                Navigation.findNavController(view)
                                    .navigate(R.id.navigateToInvestmentFragmentFromSell)
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
     * Makes call to api to retrieve customer information
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