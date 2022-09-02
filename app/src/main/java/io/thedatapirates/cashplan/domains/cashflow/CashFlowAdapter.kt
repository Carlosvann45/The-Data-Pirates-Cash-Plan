package io.thedatapirates.cashplan.domains.cashflow

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.item_expense_and_deposit.view.*

class CashFlowAdapter(
    private val expensesList: MutableList<CashFlowItem>


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

    fun addExpense(expense: CashFlowItem) {
        expensesList.add(expense)
        notifyItemInserted(expensesList.size - 1)
    }

    fun deleteItems() {
        expensesList.removeAll { expense ->
            expense.isChecked
        }
        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        if (isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val curExpense = expensesList[position]
        holder.itemView.apply {
            tvTodoTitle.text = curExpense.name
            tvExpenseValue.text = curExpense.amount.toString()
            curExpense.isChecked = curExpense.isChecked
            toggleStrikeThrough(tvTodoTitle, curExpense.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                curExpense.isChecked = !curExpense.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    fun expensesTotal(): Float {
        var total = 0f

        for (expense in expensesList) total += expense.amount

        return total
    }
}