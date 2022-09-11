package io.thedatapirates.cashplan.domains.cashflow

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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


//    fun deleteItem(id: Int) {
//        expensesList.removeAll { expense ->
//            expense.id == id
//        }
//        notifyDataSetChanged()
//    }


//    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
//        if(isChecked) {
//            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
//        } else {
//            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//        }
//    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int){
        val curExpense = expensesList[position]
        holder.itemView.apply {
            tvItemTitle.text = curExpense.name
            if (curExpense.deposits.size != 0) {
                tvExpenseValue.text = curExpense.deposits[0].amount.toString()
            } else {
                tvExpenseValue.text = "0"
            }

        }
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    fun expensesTotal() : Float {
       var total = 0f

        for (expense in expensesList) {
            if (expense.deposits.size != 0) {
                Log.i(expense.deposits.toString(), "DepositInformation")
            }

            for (deposit in expense.deposits)

            total += deposit.amount
        }

        return total
    }
}