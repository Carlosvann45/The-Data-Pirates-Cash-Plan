package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import kotlinx.android.synthetic.main.fragment_investment.view.*
import kotlinx.android.synthetic.main.fragment_investment_breakdown.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * A simple [Fragment] subclass.
 * Use the [InvestmentBreakdownFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class InvestmentBreakdownFragment : Fragment() {

    private lateinit var investmentBreakdownContext: Context
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment_breakdown, container, false)
        val title = arguments?.getString("name")
        val investmentJson = arguments?.getString("investmentTransactions")

        // creates list of investment transactions for recycler view or just an empty list
        val investmentTransactions: MutableList<InvestmentResponse> =
            if (investmentJson != null)
                Gson().fromJson(
                    investmentJson,
                    object : TypeToken<MutableList<InvestmentResponse>>() {}.type
                )
            else mutableListOf()

        // sets title with stock name
        view.tvInvestmentBreakdownTitle.text = "$title Transactions"

        // sets up recycler view and creates/adds each stock total investment to recycler
        recyclerView = view.rvInvestmentBreakdownItems
        recyclerView.layoutManager = LinearLayoutManager(investmentBreakdownContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = InvestmentBreakdownAdapter(investmentTransactions)

        view.ivInvestmentBreakdownBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.rlInvestmentFragment)
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