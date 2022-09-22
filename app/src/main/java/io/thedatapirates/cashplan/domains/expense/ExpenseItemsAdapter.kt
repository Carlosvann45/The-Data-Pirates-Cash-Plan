package io.thedatapirates.cashplan.domains.expense

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.expense.Withdrawal
import io.thedatapirates.cashplan.data.services.expense.ExpenseService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.add_expense_button.view.*
import kotlinx.android.synthetic.main.withdrawal_form.view.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
object ExpenseItemsAdapterServiceLocator {
    fun getExpenseService(): ExpenseService = ExpenseService.create()
}

@ExperimentalSerializationApi
@DelicateCoroutinesApi
class ExpenseItemsAdapter(
    private val expenses: MutableList<ExpenseResponse>,
    val view: View,
    val context: Context
) : RecyclerView.Adapter<ExpenseItemsAdapter.ExpenseItemsViewHolder>() {

    private var expenseService = ExpenseItemsAdapterServiceLocator.getExpenseService()
    private var toast: Toast? = null

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
                    val bundle = Bundle()
                    bundle.putString("fromType", "create")

                    Navigation.findNavController(view)
                        .navigate(R.id.rlAddEditExpenseFragment, bundle)
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
                val withdrawalBtn: TextView =
                    holder.itemView.findViewById(R.id.tvAddWithdrawalToExpense)
                val expenseItem: CardView = holder.itemView.findViewById(R.id.cvExpenseItem)
                val priorityText: TextView = holder.itemView.findViewById(R.id.tvExpensePriority)

                val startDate = expense.startDate.substring(0, expense.startDate.indexOf("T"))
                val endDate =
                    expense.endDate?.substring(0, expense.endDate?.indexOf("T") ?: 0) ?: ""

                expenseNameText.text = expense.name
                categoryNameText.text = expense.category.name
                frequencyNameText.text = expense.frequency.name
                startDateText.text = startDate
                endDateNameText.text = endDate
                priorityText.text = expense.priorityLevel.level

                if (!AndroidUtils.compareDates(startDate, endDate)) {
                    withdrawalBtn.visibility = View.GONE
                }

                expenseItem.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlExpenseNavFragment, bundle)
                }

                editExpenseBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))
                    bundle.putString("fromType", "edit")

                    Navigation.findNavController(view)
                        .navigate(R.id.rlAddEditExpenseFragment, bundle)
                }

                withdrawalBtn.setOnClickListener {
                    view.clWithdrawalForm.visibility = View.VISIBLE

                    view.clWithdrawalForm.setOnClickListener {
                        view.clWithdrawalForm.visibility = View.GONE
                    }

                    view.tvCloseWithdrawalForm.setOnClickListener {
                        view.clWithdrawalForm.visibility = View.GONE
                    }

                    view.cvWithdrawalFormBackground.setOnClickListener { }

                    view.btnWithdrawalForExpense.setOnClickListener {
                        // create withdrawal
                        val withdrawalAmount = view.etAmountToWithdrawal.text.toString().toDouble()

                        if (withdrawalAmount > 0.00) {
                            val withdrawal = Withdrawal(0, "", "", withdrawalAmount)

                            GlobalScope.launch(Dispatchers.IO) {
                                val updatedExpense = addWithdrawalToExpense(withdrawal, expense.id)

                                withContext(Dispatchers.Main) {
                                    if (updatedExpense != null) {
                                        expenses[position] = updatedExpense

                                        view.clWithdrawalForm.visibility = View.GONE

                                        notifyItemChanged(position)
                                    } else {
                                        toast?.cancel()

                                        toast = AndroidUtils.createCustomToast(
                                            "There was an error with the server. Please try again later.",
                                            view,
                                            context
                                        )

                                        toast?.show()
                                    }
                                }
                            }

                        } else {
                            toast?.cancel()

                            toast = AndroidUtils.createCustomToast(
                                "The amount must be greater than 0. Please try again.",
                                view,
                                context
                            )

                            toast?.show()
                        }
                    }
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

    private suspend fun addWithdrawalToExpense(
        withdrawal: Withdrawal,
        expenseId: Long
    ): ExpenseResponse? {
        val sharedPreferences =
            context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        return try {
            expenseService.addWithdrawalForExpense(accessToken, withdrawal, expenseId)
        } catch (e: Exception) {
            print("Error: ${e.message}")
            null
        }
    }
}