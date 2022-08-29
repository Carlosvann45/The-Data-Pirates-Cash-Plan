package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import kotlinx.android.synthetic.main.fragment_buy_investment.view.tvTotalAmountAvailable
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_buy_investment, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        // created a list of options for the custom picker
        val options = mutableListOf<String>()
        options.add("Choose Stock")

        GlobalScope.launch(Dispatchers.IO) {

            stockOptions = getStockTickers()

            stockOptions.forEach { options.add(it.Name) }

            withContext(Dispatchers.Main){
                // sets up the custom picker for scrolling through options
                view.npCustomPicker.minValue = 0
                view.npCustomPicker.maxValue = (options.size - 1)
                view.npCustomPicker.displayedValues = options.toTypedArray()
                view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                    val symbol = stockOptions.find { it.Name == options[newVal] }

                    if (symbol != null) view.etStockToBuy.setText(symbol.Symbol, TextView.BufferType.EDITABLE)
                    else view.etStockToBuy.setText(options[newVal], TextView.BufferType.EDITABLE)
                }
            }
        }

        // when dropdown button is selected makes sure picker is visible
        view.ivOpenSelectStockPicker.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        // when done button is selected makes sure picker is closed
        view.tvCustomPickerDone.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.GONE
        }

        view.ivBuyInvestmentBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToInvestmentFragmentFromBuyFragment)
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
}