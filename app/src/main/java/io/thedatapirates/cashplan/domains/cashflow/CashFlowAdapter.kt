package io.thedatapirates.cashplan.domains.cashflow

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import kotlinx.android.synthetic.main.item_expense_and_deposit.view.*

class CashFlowAdapter(
    private val expensesList: MutableList<CashFlowItemsResponse>

) : RecyclerView.Adapter<CashFlowAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_expense_and_deposit,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int){
        val curExpense = expensesList[position]
        holder.itemView.apply {
            tvItemTitle.text = curExpense.name
            tvCashFlowFrequencyValue.text = curExpense.frequency.name
            tvCashFlowDepositsAmount.text = curExpense.deposits.size.toString()
            if (curExpense.deposits.size != 0) {
                tvExpenseValue.text = curExpense.deposits[0].amount.toString()
            } else {
                tvExpenseValue.text = "0"
            }
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("position", position)
            Navigation.findNavController(holder.itemView).navigate(R.id.navCashFlowList, bundle)
        }
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    fun expensesTotal() : Float {
        var total = 0f

        for (expense in expensesList) {
            for (deposit in expense.deposits)  {
                total += deposit.amount
            }
        }

        return total
    }
}