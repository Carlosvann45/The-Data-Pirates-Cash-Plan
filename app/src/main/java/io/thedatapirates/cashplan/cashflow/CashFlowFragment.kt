package io.thedatapirates.cashplan.cashflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_cashflow.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*


/**
 * A simple [Fragment] subclass.
 */
class CashFlowFragment : Fragment() {

    // Initialize the Expenses List
    private lateinit var expensesList: ExpensesList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Pass in a mutable list of Expenses
        expensesList = ExpensesList(mutableListOf())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cashflow, container, false)

        view.tvCashFlowTitle.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_cashflowFragment_to_homeFragment) }

        view.btnAddExpense.setOnClickListener {
            val expenseAmount = view.etPaymentFieldAmount.text.toString().toFloat()
            if(expenseAmount != 0f) {
                val expense = Expense(expenseAmount)
                expensesList.addExpense(expense)
                view.etPaymentFieldAmount.text.clear()
            }
        }

        view.btnRemoveExpense.setOnClickListener {
                expensesList.deleteExpense()
        }

        view.tvCashInAmount.text = expensesList.getItemCount().toString()
        

        return view
    }

    fun getCashInTotal(expenseList: ExpensesList) : Float {

        val total = 0
        for(expense in 0 until expenseList.getItemCount()) {
            //total += expense.amount()
        }

        return 0.0f
    }
}