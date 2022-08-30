package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentRequest
import io.thedatapirates.cashplan.data.dtos.investment.StockTicker
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_buy_investment.view.*
import kotlinx.android.synthetic.main.fragment_buy_investment.view.ivOpenSelectStockPicker
import kotlinx.android.synthetic.main.fragment_sell_investment.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject customer service into investment fragment
 */
object BuyInvestmentServiceLocator {
    fun getInvestmentService(): InvestmentService = InvestmentService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [BuyInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class BuyInvestmentFragment : Fragment() {

    private lateinit var progressOverlay: View
    private lateinit var buyInvestmentContext: Context
    private lateinit var stockOptions: MutableList<StockTicker>
    private val investmentService = BuyInvestmentServiceLocator.getInvestmentService()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_buy_investment, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        // gets all stock tickers
        GlobalScope.launch(Dispatchers.IO) {

            stockOptions = async { getStockTickers() }.await()

        }

        // when dropdown button is selected makes sure picker is visible
        view.ivOpenSelectStockPicker.setOnClickListener {
            // created a list of options for the custom picker
            val options = mutableListOf<String>()
            options.add("Choose Stock")

            stockOptions.forEach { options.add(it.Symbol) }

            // sets up the custom picker for scrolling through options
            view.npCustomPicker.minValue = 0
            view.npCustomPicker.maxValue = (options.size - 1)
            view.npCustomPicker.displayedValues = options.toTypedArray()
            view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                view.etStockToBuy.setText(options[newVal], TextView.BufferType.EDITABLE)
            }

            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        view.ivOpenSelectSectorPicker.setOnClickListener {
            // created a list of options for the custom picker
            val options = arrayOf(
                "Technology",
                "Finance",
                "Consumer",
                "Healthcare",
                "Material",
                "Real Estate",
                "Utility",
                "Energy",
                "Industrial",
                "Communication"
            )

            // sets up the custom picker for scrolling through options
            view.npCustomPicker.minValue = 0
            view.npCustomPicker.maxValue = (options.size - 1)
            view.npCustomPicker.displayedValues = options
            view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                view.etStockSector.setText(options[newVal], TextView.BufferType.EDITABLE)
            }

            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        // when done button is selected makes sure picker is closed
        view.tvCustomPickerDone.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.GONE
        }

        // on click listener for back button
        view.ivBuyInvestmentBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToInvestmentFragmentFromBuyFragment)
        }

        // on change listener for shares calculation with amount
        view.etAmountToBuy.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) calculateAndSetShares(view, p0.toString().toDouble(), true)
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        // on change listener for shares calculation with price
        view.etPriceToBuy.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) calculateAndSetShares(view, p0.toString().toDouble(), false)
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        // runs code when buy button is clicked
        view.btnBuyStock.setOnClickListener {
            val amount = view.etAmountToBuy.text.toString()
            val price = view.etPriceToBuy.text.toString()
            val sector = view.etStockSector.text.toString()
            val stockSymbol = view.etStockToBuy.text.toString()

            if (
                amount != "" && price != "" && amount.toDouble() > 0 && price.toDouble() > 0 && stockSymbol != "Choose Stock"
            ) {

                var stockWasBought: Boolean

                GlobalScope.launch(Dispatchers.IO) {

                    val newInvestment = InvestmentRequest(
                        stockSymbol,
                        sector,
                        (amount.toDouble() / price.toDouble()),
                        price.toDouble()
                    )

                    // send buy transaction to api
                    stockWasBought = createInvestment(newInvestment)

                    if (stockWasBought) {
                        withContext(Dispatchers.Main) {
                            Navigation.findNavController(view).navigate(R.id.navigateToInvestmentFragmentFromBuyFragment)
                        }
                    } else {
                        // throw error toast
                        toast?.cancel()

                        toast = AndroidUtils.createCustomToast(
                            "There was an error with the server. Please try again later.",
                            view,
                            buyInvestmentContext
                        )

                        toast?.show()
                    }
                }
            } else {
                // throw error toast
                toast?.cancel()

                toast = AndroidUtils.createCustomToast(
                    "Amount, Price, and Stock Symbol are required.",
                    view,
                    buyInvestmentContext
                )

                toast?.show()
            }
        }

        AndroidUtils.animateView(progressOverlay, View.GONE, 0.75f, 200L)

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buyInvestmentContext = context
    }

    /**
     * Makes call to api to process sell transaction
     */
    private suspend fun getStockTickers(): MutableList<StockTicker> {
        try {
            return investmentService.getAllStockTickers()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return mutableListOf()
    }

    /**
     * Makes call to api to process sell transaction
     */
    private suspend fun createInvestment(investment: InvestmentRequest): Boolean {
        var wasSold = false
        val sharedPreferences =
            buyInvestmentContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            investmentService.createInvestment(investment, accessToken)
            wasSold = true
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return wasSold
    }

    /**
     * Calculates the current share amount and displays it
     */
    private fun calculateAndSetShares(view: View, number: Double, isAmount: Boolean) {
        val amount: Double
        val price: Double

        if (isAmount) {
            amount = number
            price = view.etPriceToBuy.text.toString().toDouble()
        } else {
            amount = view.etAmountToBuy.text.toString().toDouble()
            price = number
        }

        view.tvTotalSharesSelected.text = "You currently have ${String.format("%,.2f", amount / price)} shares selected"
    }
}