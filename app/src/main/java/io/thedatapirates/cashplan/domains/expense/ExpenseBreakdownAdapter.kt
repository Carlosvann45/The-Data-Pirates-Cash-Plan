package io.thedatapirates.cashplan.domains.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.expense.Withdrawal

class ExpenseBreakdownAdapter(
    private val transactions: MutableList<Withdrawal>
) : RecyclerView.Adapter<ExpenseBreakdownAdapter.ExpenseBreakdownViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseBreakdownViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_breakdown_item, parent, false)
        return ExpenseBreakdownViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseBreakdownViewHolder, position: Int) {
        val currentItem = transactions[position]
        val itemDate: TextView = holder.itemView.findViewById(R.id.tvExpenseBreakdownDate)
        val itemAmount: TextView = holder.itemView.findViewById(R.id.tvExpenseBreakdownAmount)

        itemDate.text = currentItem.dateCreated.substring(
            0,
            currentItem.dateCreated.indexOfFirst { it == 'T' })
        itemAmount.text = String.format("%,.2f", currentItem.amount)

    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class ExpenseBreakdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}