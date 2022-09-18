package io.thedatapirates.cashplan.domains.expense

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.domains.investment.InvestmentItemsAdapter
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.add_expense_button.view.*
import kotlinx.android.synthetic.main.add_reminder_button.view.*
import kotlinx.android.synthetic.main.expense_item.view.*
import kotlinx.android.synthetic.main.investment_buttons.view.*

class ExpenseItemsAdapter(
    private val expenses: MutableList<ExpenseResponse>,
    val view: View
) : RecyclerView.Adapter<ExpenseItemsAdapter.ExpenseItemsViewHolder>() {

    /**
     * Inflates the a layout to add it to recycler view
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseItemsAdapter.ExpenseItemsViewHolder {

        val itemView = when (viewType) {
            R.layout.add_expense_button -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_expense_button, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.expense_item, parent, false)
            }
        }

        return ExpenseItemsViewHolder(itemView)
    }

    /**
     * Creates/adds each item to the recycler view
     */
    override fun onBindViewHolder(
        holder: ExpenseItemsAdapter.ExpenseItemsViewHolder,
        position: Int
    ) {
        when (position) {
            expenses.size - 1 -> {
                holder.itemView.btnAddExpense.setOnClickListener {
                }
            }
            else -> {
                val expense = expenses[position]
                val expenseNameText: TextView = holder.itemView.findViewById(R.id.tvExpenseName)
                val categoryNameText: TextView = holder.itemView.findViewById(R.id.tvCategoryName)
                val frequencyNameText: TextView = holder.itemView.findViewById(R.id.tvFrequencyName)
                val startDateText: TextView = holder.itemView.findViewById(R.id.tvExpenseStartDate)
                val endDateNameText: TextView = holder.itemView.findViewById(R.id.tvExpenseEndDate)
                val editExpenseBtn: TextView = holder.itemView.findViewById(R.id.tvEditExpense)
                val withdrawalBtn: TextView = holder.itemView.findViewById(R.id.tvAddWithdrawalToExpense)
                val expenseItem: CardView = holder.itemView.findViewById(R.id.cvExpenseItem)

                val startDate = expense.startDate.substring(0, expense.startDate.indexOf("T"))
                val endDate = expense.endDate?.substring(0, expense.endDate?.indexOf("T")?.minus(1) ?: 0) ?: ""

                expenseNameText.text = expense.name
                categoryNameText.text = expense.category.name
                frequencyNameText.text = expense.frequency.name
                startDateText.text = startDate
                endDateNameText.text = endDate

                if (!AndroidUtils.compareDates(startDate, endDate)) {
                    withdrawalBtn.visibility = View.GONE
                }

                expenseItem.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlExpenseNavFragment, bundle)
                }

                editExpenseBtn.setOnClickListener {

                }

                withdrawalBtn.setOnClickListener {

                }
            }
        }
    }

    /**
     * Decides on what layout to return based on position
     */
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            expenses.size - 1 -> {
                R.layout.add_expense_button
            }
            else -> {
                R.layout.investment_item
            }
        }
    }


    /**
     * Tells recycler view how many items are in the array
     */
    override fun getItemCount(): Int {
        return expenses.size
    }

    /**
     * Gets the different items from the view to modify each field on a given layout
     */
    class ExpenseItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}