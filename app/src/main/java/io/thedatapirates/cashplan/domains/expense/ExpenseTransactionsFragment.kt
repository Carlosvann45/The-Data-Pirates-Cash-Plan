package io.thedatapirates.cashplan.domains.expense

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
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.domains.investment.InvestmentBreakdownAdapter
import kotlinx.android.synthetic.main.fragment_expense_transactions.view.*
import kotlinx.android.synthetic.main.fragment_investment_breakdown.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpenseTransactionsFragment : Fragment() {

    private lateinit var expenseBreakdownContext: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var expense: ExpenseResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense_transactions, container, false)
        val expenseJSON = arguments?.getString("expense")

        expense =
            if (expenseJSON != null)
                Gson().fromJson(
                    expenseJSON,
                    object : TypeToken<ExpenseResponse>() {}.type
                )
            else ExpenseResponse()

        // sets title with stock name
        view.tvExpenseBreakdownTitle.text = "${expense.name} Transactions"

        // sets up recycler view and creates/adds each stock total investment to recycler
        recyclerView = view.rvExpenseBreakdownItems
        recyclerView.layoutManager = LinearLayoutManager(expenseBreakdownContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ExpenseBreakdownAdapter(expense.withdrawals)

        view.ivExpenseBreakdownBackBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("expense", Gson().toJson(expense))

            Navigation.findNavController(view).navigate(R.id.rlExpenseNavFragment, bundle)
        }

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        expenseBreakdownContext = context
    }

}