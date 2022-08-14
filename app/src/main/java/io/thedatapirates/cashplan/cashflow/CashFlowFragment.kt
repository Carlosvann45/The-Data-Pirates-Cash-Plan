package io.thedatapirates.cashplan.cashflow

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_cashflow.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlin.math.log


/**
 * A simple [Fragment] subclass.
 */
class CashFlowFragment : Fragment() {

    // Initialize the Expenses List
    private lateinit var expensesList: ExpensesList
    // Initialize the Deposits List
    private lateinit var depositsList: DepositsList


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Pass in a mutable list of Expenses
        expensesList = ExpensesList(mutableListOf())
        // Pass in a mutable list of Deposits
        depositsList = DepositsList(mutableListOf())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cashflow, container, false)

        // Sets the click listener on the cashflow title to go back to the home_fragment
        view.tvCashFlowTitle.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_cashflowFragment_to_homeFragment) }

        // EXPENSES
        // Functionality to add an expense to a list of expenses
        view.btnAddExpense.setOnClickListener {
            if (view.etPaymentFieldAmount.text.isNotEmpty()){
                val expenseAmount = view.etPaymentFieldAmount.text.toString().toFloat()
                val expense = Expense("expense:", expenseAmount)
                expensesList.addExpense(expense)
                view.etPaymentFieldAmount.text.clear()

                view.tvCashOutAmount.text = "Cash Out: " + expensesList.getExpensesTotal().toString()
                view.tvProfit.text = "Profit: " + (depositsList.getDepositsTotal() - expensesList.getExpensesTotal()).toString()
            }
        }

        // Removes an expense based on the expense name passed in
        view.btnRemoveExpense.setOnClickListener {
            if (expensesList.getItemCount() > 0) {
                expensesList.deleteExpense("expense:")
            }
            view.tvCashOutAmount.text = "Cash Out: " + expensesList.getExpensesTotal().toString()
        }

        // DEPOSITS
        // Functionality to add a deposit to a list of deposits
        view.btnAddDeposit.setOnClickListener {
            if (view.etDepositFieldAmount.text.isNotEmpty()){
                val depositAmount = view.etDepositFieldAmount.text.toString().toFloat()
                val deposit = Deposit("deposit:", depositAmount)
                depositsList.addDeposit(deposit)
                view.etDepositFieldAmount.text.clear()

                view.tvCashInAmount.text = "Cash In: " + depositsList.getDepositsTotal().toString()
                view.tvProfit.text = "Profit: " + (depositsList.getDepositsTotal() - expensesList.getExpensesTotal()).toString()
            }
        }

        // Removes a deposit based on the expense name passed in
        view.btnRemoveDeposit.setOnClickListener {
            if (depositsList.getItemCount() > 0) {
                depositsList.deleteDeposit("deposit:")
            }
            view.tvCashInAmount.text = "Cash In: " + depositsList.getDepositsTotal().toString()
        }



        return view
    }

}