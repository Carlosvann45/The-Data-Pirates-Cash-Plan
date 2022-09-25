package io.thedatapirates.cashplan.domains.expense

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderResponse
import io.thedatapirates.cashplan.data.services.reminder.ReminderService
import io.thedatapirates.cashplan.utils.AlarmReceiver
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.add_reminder_button.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject customer service into login fragment
 */
object ReminderAdapterServiceLocator {
    fun getReminderService(): ReminderService = ReminderService.create()
}

@DelicateCoroutinesApi
class ReminderBreakdownAdapter(
    private val reminders: MutableList<ReminderResponse>,
    private val expense: ExpenseResponse,
    val view: View,
    val context: Context,
    private var toast: Toast? = null
) : RecyclerView.Adapter<ReminderBreakdownAdapter.ReminderItemsViewHolder>() {

    private val reminderService = AddExpenseServiceLocator.getReminderService()

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
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ReminderBreakdownAdapter.ReminderItemsViewHolder,
        position: Int
    ) {
        when (position) {
            reminders.size - 1 -> {
                holder.itemView.btnAddReminder.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlAddReminderFragment, bundle)
                }
            }
            else -> {
                val reminder = reminders[position]
                val reminderItem: CardView = holder.itemView.findViewById(R.id.cvReminderItem)
                val reminderNameText: TextView = holder.itemView.findViewById(R.id.tvReminderName)
                val reminderDateText: TextView = holder.itemView.findViewById(R.id.tvReminderDate)
                val frequencyNameText: TextView = holder.itemView.findViewById(R.id.tvFrequencyName)
                val reminderDescriptionText: TextView =
                    holder.itemView.findViewById(R.id.tvReminderDescription)
                val deleteReminderBtn: TextView =
                    holder.itemView.findViewById(R.id.tvDeleteReminder)

                val endOfDate = reminder.reminderTime.indexOf("T")
                val reminderDate = reminder.reminderTime.substring(0, endOfDate)
                val reminderTime = reminder.reminderTime.substring(endOfDate + 1, endOfDate + 6)

                reminderNameText.text = reminder.name
                reminderDateText.text = reminderDate.plus(" ").plus(reminderTime)
                frequencyNameText.text = reminder.frequency.name
                reminderDescriptionText.text = reminder.description

                reminderItem.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlExpenseNavFragment, bundle)
                }

                deleteReminderBtn.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        val deleted = deleteReminderById(reminder.id)

                        withContext(Dispatchers.Main) {
                            if (deleted) {
                                val alarmManager =
                                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                                val intent = Intent(context, AlarmReceiver::class.java)

                                val pendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    reminder.id.toInt(),
                                    intent,
                                    PendingIntent.FLAG_IMMUTABLE
                                )

                                alarmManager.cancel(pendingIntent)

                                expense.reminders.removeAt(reminders.indexOfFirst { it.id == reminder.id })
                                reminders.removeAt(reminders.indexOfFirst { it.id == reminder.id })

                                notifyDataSetChanged()
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
    class ReminderItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private suspend fun deleteReminderById(id: Long): Boolean {
        val sharedPreferences =
            context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        return try {
            reminderService.deleteReminder(accessToken, id)
            true
        } catch (e: Exception) {
            print("Error: ${e.message}")
            false
        }
    }
}