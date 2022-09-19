package io.thedatapirates.cashplan.domains.expense

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderRequest
import io.thedatapirates.cashplan.data.services.frequency.FrequencyService
import io.thedatapirates.cashplan.data.services.reminder.ReminderService
import io.thedatapirates.cashplan.utils.AlarmReceiver
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_add_reminder.view.*
import kotlinx.android.synthetic.main.fragment_buy_investment.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.android.synthetic.main.reminder_item.view.*
import kotlinx.coroutines.*
import java.util.*

/**
 * Service locator to inject customer service into login fragment
 */
object AddExpenseServiceLocator {
    fun getFrequencyService(): FrequencyService = FrequencyService.create()
    fun getReminderService(): ReminderService = ReminderService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [AddReminderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class AddReminderFragment : Fragment() {

    private lateinit var addReminderContext: Context
    private lateinit var expense: ExpenseResponse
    private lateinit var progressOverlay: View
    private lateinit var frequencies: MutableList<Frequency>
    private var dateDialog: DatePickerDialog? = null
    private var timeDialog: TimePickerDialog? = null
    private var alarmTime: Long = 0L
    private val frequencyService = AddExpenseServiceLocator.getFrequencyService()
    private val reminderService = AddExpenseServiceLocator.getReminderService()
    private var toast: Toast? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_reminder, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        val expenseJSON = arguments?.getString("expense")

        expense =
            if (expenseJSON != null)
                Gson().fromJson(
                    expenseJSON,
                    object : TypeToken<ExpenseResponse>() {}.type
                )
            else ExpenseResponse()

        GlobalScope.launch(Dispatchers.IO) {
            frequencies = getAllFrequencies()

            withContext(Dispatchers.Main) {
                // when dropdown button is selected makes sure picker is visible
                view.ivOpenSelectFrequency.setOnClickListener {
                    // created a list of options for the custom picker
                    val options = mutableListOf<String>()
                    options.add("Choose Option")

                    frequencies.forEach { options.add(it.name) }

                    // sets up the custom picker for scrolling through options
                    view.npCustomPicker.minValue = 0
                    view.npCustomPicker.maxValue = (options.size - 1)
                    view.npCustomPicker.displayedValues = options.toTypedArray()
                    view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                        view.etFrequencyText.setText(options[newVal], TextView.BufferType.EDITABLE)
                    }

                    view.clCustomPickerLayout.visibility = View.VISIBLE
                }

                view.tvCustomPickerDone.setOnClickListener {
                    view.clCustomPickerLayout.visibility = View.GONE
                }

                view.ivCreateReminderBackBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("expense", Gson().toJson(expense))

                    Navigation.findNavController(view).navigate(R.id.rlExpenseRemindersFragment)
                }

                view.ivOpenSelectTime.setOnClickListener {
                    selectTimeAndDate(view)
                }

                view.btnCreateReminder.setOnClickListener {
                    val validResponse: Boolean
                    val nameText = view.etNameText.text.toString()
                    val descriptionText = view.etDescriptionText.text.toString()
                    val reminderTime = view.etReminderTime.text.toString()
                    val frequency =
                        frequencies.find { it.name == view.etFrequencyText.text.toString() }

                    validResponse =
                        nameText.isNotEmpty() && descriptionText.isNotEmpty() && frequency != null && reminderTime != "Choose Time"

                    if (validResponse) {
                        val reminder = ReminderRequest(
                            nameText,
                            descriptionText,
                            reminderTime,
                            frequency!!.id,
                            expense.id
                        )

                        GlobalScope.launch(Dispatchers.IO) {
                            createReminderAndNavigate(view, reminder)
                        }
                    } else {
                        toast?.cancel()

                        toast = AndroidUtils.createCustomToast(
                            "Name, Description, Frequency, and Time are Required. Please try again.",
                            view,
                            addReminderContext
                        )

                        toast?.show()
                    }
                }

                AndroidUtils.animateView(progressOverlay, View.GONE, 0.75f, 200L)
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dateDialog != null) {
            dateDialog!!.dismiss()
        }
        if (timeDialog !== null) {
            timeDialog!!.dismiss()
        }
    }

    private fun selectTimeAndDate(view: View) {
        Calendar.getInstance().apply {
            dateDialog = DatePickerDialog(
                addReminderContext,
                R.style.MyDatePickerStyle,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)

                    timeDialog = TimePickerDialog(
                        addReminderContext,
                        R.style.MyDatePickerStyle,
                        { _, hour, min ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, min)
                            this.set(Calendar.SECOND, 0)
                            this.set(Calendar.MILLISECOND, 0)

                            view.etReminderTime.text = if (hour > 12) {
                                "$year-${formatDateOrTime(month + 1)}-${formatDateOrTime(day)} ${
                                    formatDateOrTime(
                                        hour - 12
                                    )
                                }:${formatDateOrTime(min)}"
                            } else {
                                "$year-${formatDateOrTime(month + 1)}-${formatDateOrTime(day)} ${
                                    formatDateOrTime(
                                        hour
                                    )
                                }:${formatDateOrTime(min)}"
                            }

                            alarmTime = this.timeInMillis
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    )

                    timeDialog!!.show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            )

            dateDialog!!.show()
        }
    }

    private fun formatDateOrTime(dateOrTime: Int): String {
        return if (dateOrTime < 10) {
            "0$dateOrTime"
        } else dateOrTime.toString()
    }

    private suspend fun createReminderAndNavigate(view: View, reminder: ReminderRequest) {
        val sharedPreferences =
            addReminderContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        val reminderResponse = try {
            reminderService.createReminder(accessToken, reminder)
        } catch (e: Exception) {
            print("Error: ${e.message}")
            null
        }

        withContext(Dispatchers.Main) {
            if (reminderResponse != null) {
                expense.reminders.add(reminderResponse)

                val alarmManager =
                    requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val intent = Intent(addReminderContext, AlarmReceiver::class.java).apply {
                    putExtra("id", reminderResponse.id.toInt())
                    putExtra("reminder", Gson().toJson(reminderResponse))
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    addReminderContext,
                    reminderResponse.id.toInt(),
                    intent,
                    0
                )

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    getTimeInterval(reminderResponse.frequency.name),
                    pendingIntent
                )

                val bundle = Bundle()
                bundle.putString("expense", Gson().toJson(expense))

                Navigation.findNavController(view).navigate(R.id.rlExpenseRemindersFragment, bundle)
            } else {
                toast?.cancel()

                toast = AndroidUtils.createCustomToast(
                    "There was an issue with the server. Please try again later.",
                    view,
                    addReminderContext
                )

                toast?.show()
            }
        }
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        addReminderContext = context
    }

    private suspend fun getAllFrequencies(): MutableList<Frequency> {
        return try {
            frequencyService.getFrequencies()
        } catch (e: Exception) {
            print("Error: ${e.message}")
            mutableListOf()
        }
    }

    private fun getTimeInterval(intervalName: String): Long {
        return when (intervalName) {
            "Daily" -> AlarmManager.INTERVAL_DAY
            "Weekly" -> AlarmManager.INTERVAL_DAY * 7
            "Biweekly" -> AlarmManager.INTERVAL_DAY * 14
            "Monthly" -> AlarmManager.INTERVAL_DAY * Calendar.getInstance()
                .getActualMaximum(Calendar.DAY_OF_MONTH)
            "Yearly" -> AlarmManager.INTERVAL_DAY * 365
            else -> 0L

        }
    }
}