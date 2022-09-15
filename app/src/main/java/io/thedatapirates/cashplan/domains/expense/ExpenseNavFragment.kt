package io.thedatapirates.cashplan.domains.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import kotlinx.android.synthetic.main.add_expense_button.view.*
import kotlinx.android.synthetic.main.fragment_expense_nav.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseNavFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpenseNavFragment : Fragment() {

    private lateinit var expense: ExpenseResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense_nav, container, false)
        val expenseJSON = arguments?.getString("expense")

        expense =
            if (expenseJSON != null)
                Gson().fromJson(
                    expenseJSON,
                    object : TypeToken<ExpenseResponse>() {}.type
                )
            else ExpenseResponse()

        view.ivExpenseOptionBackBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.rlExpenseFragment)
        }

        view.btnExpenseTransactions.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("expense", Gson().toJson(expense))

            Navigation.findNavController(view).navigate(R.id.rlExpenseTransactionsFragment, bundle)
        }

        view.btnReminders.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("expense", Gson().toJson(expense))

        }

        return view
    }

}