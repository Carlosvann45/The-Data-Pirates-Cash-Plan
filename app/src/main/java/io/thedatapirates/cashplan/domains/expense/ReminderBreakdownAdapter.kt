package io.thedatapirates.cashplan.domains.expense

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
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderResponse
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.add_expense_button.view.*
import kotlinx.android.synthetic.main.add_reminder_button.view.*

class ReminderBreakdownAdapter(
    private val reminders: MutableList<ReminderResponse>,
    private val expense: ExpenseResponse,
    val view: View
) : RecyclerView.Adapter<ReminderBreakdownAdapter.ReminderItemsViewHolder>() {

    /**
     * Inflates the a layout to add it to recycler view
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderBreakdownAdapter.ReminderItemsViewHolder {

        val itemView = when (viewType) {
            R.layout.add_reminder_button -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_reminder_button, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reminder_item, parent, false)
            }
        }

        return ReminderItemsViewHolder(itemView)
    }

    /**
     * Creates/adds each item to the recycler view
     */
    override fun onBindViewHolder(
        holder: ReminderBreakdownAdapter.ReminderItemsViewHolder,
        position: Int
    ) {
        when (position) {
            reminders.size - 1 -> {
                holder.itemView.btnAddReminder.setOnClickListener {
                }
            }
            else -> {
                val reminder = reminders[position]
                val reminderItem: CardView = holder.itemView.findViewById(R.id.cvReminderItem)
                val reminderNameText: TextView = holder.itemView.findViewById(R.id.tvReminderName)
                val reminderDateText: TextView = holder.itemView.findViewById(R.id.tvReminderDate)
                val frequencyNameText: TextView = holder.itemView.findViewById(R.id.tvFrequencyName)
                val reminderDescriptionText: TextView = holder.itemView.findViewById(R.id.tvReminderDescription)
                val deleteReminderBtn: TextView = holder.itemView.findViewById(R.id.tvDeleteReminder)

                val reminderDate = reminder.reminderTime.substring(0, reminder.reminderTime.indexOf("T") - 1)

                reminderNameText.text = reminder.name
                reminderDateText.text = reminderDate
                frequencyNameText.text = reminder.frequency.name
                reminderDescriptionText.text = reminder.description

                reminderItem.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlExpenseNavFragment, bundle)
                }

                deleteReminderBtn.setOnClickListener {

                }
            }
        }
    }

    /**
     * Decides on what layout to return based on position
     */
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            reminders.size - 1 -> {
                R.layout.add_reminder_button
            }
            else -> {
                R.layout.reminder_item
            }
        }
    }


    /**
     * Tells recycler view how many items are in the array
     */
    override fun getItemCount(): Int {
        return reminders.size
    }

    /**
     * Gets the different items from the view to modify each field on a given layout
     */
    class ReminderItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}