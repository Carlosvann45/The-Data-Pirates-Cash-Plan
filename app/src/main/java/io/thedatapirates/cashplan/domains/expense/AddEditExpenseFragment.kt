package io.thedatapirates.cashplan.domains.expense

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
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
import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseRequest
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import io.thedatapirates.cashplan.data.dtos.priortiyLevel.PriorityLevel
import io.thedatapirates.cashplan.data.services.category.CategoryService
import io.thedatapirates.cashplan.data.services.expense.ExpenseService
import io.thedatapirates.cashplan.data.services.frequency.FrequencyService
import io.thedatapirates.cashplan.data.services.priorityLevel.PriorityLevelService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_add_edit_expense.view.*
import kotlinx.android.synthetic.main.fragment_add_reminder.view.*
import kotlinx.android.synthetic.main.fragment_add_reminder.view.etFrequencyText
import kotlinx.android.synthetic.main.fragment_add_reminder.view.etNameText
import kotlinx.android.synthetic.main.fragment_add_reminder.view.ivOpenSelectFrequency
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

/**
 * Service locator to inject customer service into login fragment
 */

@ExperimentalSerializationApi
object AddEditExpenseServiceLocator {
    fun getCategoryService(): CategoryService = CategoryService.create()
    fun getFrequencyService(): FrequencyService = FrequencyService.create()
    fun getPriorityLevelService(): PriorityLevelService = PriorityLevelService.create()
    fun getExpenseService(): ExpenseService = ExpenseService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditExpenseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@ExperimentalSerializationApi
@DelicateCoroutinesApi
class AddEditExpenseFragment : Fragment() {

    private lateinit var addEditExpenseContext: Context
    private lateinit var progressOverlay: View
    private lateinit var frequencies: MutableList<Frequency>
    private lateinit var categories: MutableList<Category>
    private lateinit var priorityLevels: MutableList<PriorityLevel>
    private val frequencyService = AddEditExpenseServiceLocator.getFrequencyService()
    private val categoryService = AddEditExpenseServiceLocator.getCategoryService()
    private val expenseService = AddEditExpenseServiceLocator.getExpenseService()
    private val priorityLevelService = AddEditExpenseServiceLocator.getPriorityLevelService()
    private var dateDialog: DatePickerDialog? = null
    private var expense: ExpenseResponse? = null
    private var formType: String? = null
    private var toast: Toast? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_edit_expense, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        val expenseJSON = arguments?.getString("expense")

        formType = arguments?.getString("fromType")
        expense =
            if (expenseJSON != null)
                Gson().fromJson(
                    expenseJSON,
                    object : TypeToken<ExpenseResponse>() {}.type
                )
            else null

        if (formType == "create" && expense == null) {
            view.tvAddEditExpenseTitle.text = "Create Expense"
            view.btnSubmitExpense.text = "Create Expense"
        } else {
            view.tvAddEditExpenseTitle.text = "Edit Expense"
            view.btnSubmitExpense.text = "Edit Expense"
            view.etFrequencyText.text = expense!!.frequency.name
            view.etCategoryText.text = expense!!.category.name
            view.etPriorityLevelText.text = expense!!.priorityLevel.level
            view.tvStartDateText.text =
                expense!!.startDate.substring(0, expense!!.startDate.indexOf("T"))
            view.tvEndDateText.text =
                expense!!.endDate?.substring(0, expense!!.endDate!!.indexOf("T")) ?: "Choose Date"
            view.etNameText.text = Editable.Factory.getInstance().newEditable(expense!!.name)
        }

        GlobalScope.launch(Dispatchers.IO) {
            frequencies = getAllFrequencies()
            categories = getAllCategories()
            priorityLevels = getAllPriorityLevels()

            withContext(Dispatchers.Main) {
                // when dropdown button is selected makes sure picker is visible
                view.ivOpenSelectFrequency.setOnClickListener {
                    // created a list of options for the custom picker
                    val options = mutableListOf<String>()
                    if (formType == "create" && expense == null) {
                        options.add("Choose Option")
                        frequencies.forEach { options.add(it.name) }
                    } else {
                        options.add((frequencies.find { it.name == expense!!.frequency.name }!!.name))
                        options.add("Choose Option")
                        frequencies.forEach {
                            if (it.name != expense!!.frequency.name) options.add(
                                it.name
                            )
                        }
                    }

                    // sets up the custom picker for scrolling through options
                    view.npCustomPicker.minValue = 0
                    view.npCustomPicker.maxValue = (options.size - 1)
                    view.npCustomPicker.displayedValues = options.toTypedArray()
                    view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                        view.etFrequencyText.setText(options[newVal], TextView.BufferType.EDITABLE)
                    }

                    view.clCustomPickerLayout.visibility = View.VISIBLE
                }

                // when dropdown button is selected makes sure picker is visible
                view.ivOpenSelectCategory.setOnClickListener {
                    // created a list of options for the custom picker
                    val options = mutableListOf<String>()
                    if (formType == "create" && expense == null) {
                        options.add("Choose Option")
                        categories.forEach { options.add(it.name) }
                    } else {
                        options.add((categories.find { it.name == expense!!.category.name }!!.name))
                        options.add("Choose Option")
                        categories.forEach { if (it.name != expense!!.category.name) options.add(it.name) }
                    }

                    // sets up the custom picker for scrolling through options
                    view.npCustomPicker.minValue = 0
                    view.npCustomPicker.maxValue = (options.size - 1)
                    view.npCustomPicker.displayedValues = options.toTypedArray()
                    view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                        view.etCategoryText.setText(options[newVal], TextView.BufferType.EDITABLE)
                    }

                    view.clCustomPickerLayout.visibility = View.VISIBLE
                }

                // when dropdown button is selected makes sure picker is visible
                view.ivOpenSelectPriority.setOnClickListener {
                    // created a list of options for the custom picker
                    val options = mutableListOf<String>()
                    if (formType == "create" && expense == null) {
                        options.add("Choose Option")
                        priorityLevels.forEach { options.add(it.level) }
                    } else {
                        options.add((priorityLevels.find { it.level == expense!!.priorityLevel.level }!!.level))
                        options.add("Choose Option")
                        priorityLevels.forEach {
                            if (it.level != expense!!.priorityLevel.level) options.add(
                                it.level
                            )
                        }
                    }

                    // sets up the custom picker for scrolling through options
                    view.npCustomPicker.minValue = 0
                    view.npCustomPicker.maxValue = (options.size - 1)
                    view.npCustomPicker.displayedValues = options.toTypedArray()
                    view.npCustomPicker.setOnValueChangedListener { _, _, newVal ->
                        view.etPriorityLevelText.setText(
                            options[newVal],
                            TextView.BufferType.EDITABLE
                        )
                    }

                    view.clCustomPickerLayout.visibility = View.VISIBLE
                }

                view.tvCustomPickerDone.setOnClickListener {
                    view.clCustomPickerLayout.visibility = View.GONE
                }

                view.ivAddEditExpenseBackBtn.setOnClickListener {
                    Navigation.findNavController(view).navigate(R.id.rlExpenseFragment)
                }

                view.ivOpenSelectStartDate.setOnClickListener {
                    selectTimeAndDate(view, "start")
                }

                view.ivOpenSelectEndDate.setOnClickListener {
                    selectTimeAndDate(view, "end")
                }

                view.btnSubmitExpense.setOnClickListener {
                    val expenseId = expense?.id ?: 0
                    val nameText = view.etNameText.text.toString()
                    val frequency =
                        frequencies.find { it.name == view.etFrequencyText.text.toString() }
                    val category =
                        categories.find { it.name == view.etCategoryText.text.toString() }
                    val priority =
                        priorityLevels.find { it.level == view.etPriorityLevelText.text.toString() }
                    val startDate = view.tvStartDateText.text.toString().plus(" 00:00")
                    var endDate: String? = view.tvEndDateText.text.toString()

                    endDate = if (endDate == "Choose Date") null
                    else endDate.plus(" 00:00")

                    val validName = nameText.isNotEmpty()
                    val validIds = frequency != null && category != null && priority != null
                    val validStartDate = startDate != "Choose Date"

                    if (validName && validStartDate && validIds) {

                        val newExpense = ExpenseRequest(
                            expenseId,
                            nameText,
                            startDate,
                            endDate,
                            frequency!!.id,
                            category!!.id,
                            priority!!.id
                        )

                        GlobalScope.launch(Dispatchers.IO) {
                            createEditExpenseAndNavigate(view, newExpense)
                        }
                    } else {
                        toast?.cancel()

                        toast = AndroidUtils.createCustomToast(
                            "Name, Frequency, Category, Priority, and Start Date are required. Please try again.",
                            view,
                            addEditExpenseContext
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
    }

    private fun selectTimeAndDate(view: View, dateToSelect: String) {
        Calendar.getInstance().apply {
            dateDialog = DatePickerDialog(
                addEditExpenseContext,
                R.style.MyDatePickerStyle,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)

                    if (dateToSelect == "start") {
                        view.tvStartDateText.text =
                            "$year-${formatDateOrTime(month + 1)}-${formatDateOrTime(day)}"
                    } else {
                        view.tvEndDateText.text =
                            "$year-${formatDateOrTime(month + 1)}-${formatDateOrTime(day)}"
                    }

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

    private suspend fun createEditExpenseAndNavigate(view: View, newExpense: ExpenseRequest) {
        val sharedPreferences =
            addEditExpenseContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        val expenseResponse = if (formType == "create" && expense == null) {
            try {
                expenseService.createExpense(accessToken, newExpense)
            } catch (e: Exception) {
                print("Error: ${e.message}")
                null
            }
        } else {
            try {
                expenseService.editExpense(accessToken, newExpense)
            } catch (e: Exception) {
                print("Error: ${e.message}")
                null
            }
        }

        withContext(Dispatchers.Main) {
            if (expenseResponse != null) {
                Navigation.findNavController(view).navigate(R.id.rlExpenseFragment)
            } else {
                toast?.cancel()

                toast = AndroidUtils.createCustomToast(
                    "There was an issue with the server. Please try again later.",
                    view,
                    addEditExpenseContext
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
        addEditExpenseContext = context
    }

    private suspend fun getAllFrequencies(): MutableList<Frequency> {
        return try {
            frequencyService.getFrequencies()
        } catch (e: Exception) {
            print("Error: ${e.message}")
            mutableListOf()
        }
    }

    private suspend fun getAllCategories(): MutableList<Category> {
        return try {
            categoryService.getCategories()
        } catch (e: Exception) {
            print("Error: ${e.message}")
            mutableListOf()
        }
    }

    private suspend fun getAllPriorityLevels(): MutableList<PriorityLevel> {
        return try {
            priorityLevelService.getPriorityLevels()
        } catch (e: Exception) {
            print("Error: ${e.message}")
            mutableListOf()
        }
    }
}