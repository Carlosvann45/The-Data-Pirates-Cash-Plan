package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_sell_investment.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [SellInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellInvestmentFragment : Fragment() {

    private lateinit var investmentBreakdownContext: Context
    private lateinit var totalInvestments: MutableList<TotalInvestment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sell_investment, container, false)
        val listJson = arguments?.getString("stockList")

        totalInvestments =
            if (listJson != null)
                Gson().fromJson(listJson, object: TypeToken<MutableList<TotalInvestment>>(){}.type)
            else mutableListOf()


        val options = mutableListOf<String>()
        options.add("Choose Stock")

        totalInvestments.forEach { if (it.name != "") options.add(it.name) }

        view.npCustomPicker.minValue = 0
        view.npCustomPicker.maxValue = (options.size - 1)
        view.npCustomPicker.displayedValues = options.toTypedArray()
        view.npCustomPicker.setOnValueChangedListener {
                _, _, newVal ->
            view.etStockToSell.setText(options[newVal], TextView.BufferType.EDITABLE)

            val amount: Double = totalInvestments.find {
                it.name == view.etStockToSell.text.toString()
            }?.currentAmount ?: 0.01

            view.tvTotalAmountAvailable.text = "You currently have $${String.format("%,.2f", amount)} available"
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

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        investmentBreakdownContext = context
    }

}