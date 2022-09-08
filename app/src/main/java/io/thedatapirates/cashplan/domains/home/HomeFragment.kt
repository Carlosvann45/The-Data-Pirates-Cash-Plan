package io.thedatapirates.cashplan.domains.home

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.expense.Withdrawal
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.category.CategoryService
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.data.services.expense.ExpenseService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.home_monthly_tracker.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.coroutines.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import java.text.DecimalFormat
import java.time.LocalDateTime.now
import java.util.*

/**
 * Service locator to inject customer service into login fragment
 */
object HomeServiceLocator {
    fun getExpenseService(): ExpenseService = ExpenseService.create()
    fun getCategoryService(): CategoryService = CategoryService.create()
}

/**
 * A simple [Fragment] subclass.
 */
@DelicateCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var homeContext: Context
    private lateinit var progressOverlay: View
    private lateinit var overviewPieChart: PieChart
    private lateinit var expenses: MutableList<ExpenseResponse>
    private lateinit var categories: MutableList<Category>
    private val expenseService = HomeServiceLocator.getExpenseService()
    private val categoryService = HomeServiceLocator.getCategoryService()
    private var expensesMap = mutableMapOf<String, Double>()

    /**
     * Runs listener's when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        GlobalScope.launch(Dispatchers.IO) {

            categories = getCategories()
            expenses = getExpensesForCustomer()

            var totalOverviewAmount = 0.00

            for (category in categories) {
                val uniqueExpenses = expenses.filter {
                    it.category.name == category.name &&
                            AndroidUtils.compareDates(it.startDate, it.endDate)
                }

                var totalCategoryAmount = 0.00

                uniqueExpenses.forEach { expense ->
                    expense.withdrawals.filter {
                        AndroidUtils.compareCurrentMonth(it.dateCreated)
                    }.forEach {
                        totalCategoryAmount += it.amount
                        totalOverviewAmount += it.amount
                    }
                }

                expensesMap[category.name] = totalOverviewAmount
            }

            withContext(Dispatchers.Main) {
                initializeMonthlyTracker(view, totalOverviewAmount)

                AndroidUtils.animateView(progressOverlay, View.GONE, 0f, 200L)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContext = context
    }

    private suspend fun getExpensesForCustomer() : MutableList<ExpenseResponse> {
        val sharedPreferences =
            homeContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        return try {
            expenseService.getExpensesForCustomer(accessToken)
        } catch(e: Exception) {
            mutableListOf()
        }
    }

    private suspend fun getCategories(): MutableList<Category> {
        return try {
            categoryService.getCategories()
        } catch(e: Exception) {
            mutableListOf()
        }
    }

    private fun initializeMonthlyTracker(view: View, totalAmount: Double) {
        val pieEntries = ArrayList<PieEntry>()

        val pieColors = ArrayList<Int>()

        // sets up pie entries and colors for pie chart
        pieColors.add(findColorResource("Housing"))
        pieEntries.add(PieEntry(((expensesMap["Housing"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Transportation"))
        pieEntries.add(PieEntry(((expensesMap["Transportation"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Food"))
        pieEntries.add(PieEntry(((expensesMap["Food"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Utilities"))
        pieEntries.add(PieEntry(((expensesMap["Utilities"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Insurance"))
        pieEntries.add(PieEntry(((expensesMap["Insurance"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Medical"))
        pieEntries.add(PieEntry(((expensesMap["Medical"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Savings"))
        pieEntries.add(PieEntry(((expensesMap["Savings"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Personal"))
        pieEntries.add(PieEntry(((expensesMap["Personal"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Recreation"))
        pieEntries.add(PieEntry(((expensesMap["Recreation"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        pieColors.add(findColorResource("Miscellaneous"))
        pieEntries.add(PieEntry(((expensesMap["Miscellaneous"]?.div(totalAmount))?.times(100))?.toFloat() ?: 0f))

        // sets text
        val monthText = view.tvMonthSpendingTitle
        val totalAmountText = view.tvAmountSpendingTitle
        val month = AndroidUtils.getMonthName()
        val formattedTotal = "$${String.format("%,.2f", totalAmount)}"


        monthText.text = "$month spending tracker"
        totalAmountText.text = "You've spent $formattedTotal so far this month"

        // sets up the pie chart
        overviewPieChart = view.pcWithdrawalPieChart
        overviewPieChart.setUsePercentValues(true)
        overviewPieChart.minAngleForSlices = 15f

        if (expensesMap.values.isEmpty()) {
            overviewPieChart.description.isEnabled = false
        } else {
            val description = Description()

            description.text = "No expenses have been paid"
            description.textColor = resources.getColor(R.color.white)
            description.textSize = 12f
            description.typeface = Typeface.DEFAULT_BOLD
            description.xOffset = 115f
            description.yOffset = 180f

            overviewPieChart.description.isEnabled = true
            overviewPieChart.description = description
        }

        overviewPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        overviewPieChart.dragDecelerationFrictionCoef = 0.5f
        overviewPieChart.isDrawHoleEnabled = true
        overviewPieChart.setHoleColor(resources.getColor(R.color.blue))
        overviewPieChart.setTransparentCircleColor(resources.getColor(R.color.black))
        overviewPieChart.setTransparentCircleAlpha(110)
        overviewPieChart.holeRadius = 65f
        overviewPieChart.transparentCircleRadius = 70f
        overviewPieChart.setDrawCenterText(false)
        overviewPieChart.rotationAngle = 0f
        overviewPieChart.isRotationEnabled = true
        overviewPieChart.isHighlightPerTapEnabled = true
        overviewPieChart.animateY(1400, Easing.EaseInOutQuad)
        overviewPieChart.legend.isEnabled = false


        val pieDataSet = PieDataSet(pieEntries, "")

        pieDataSet.setDrawIcons(false)
        pieDataSet.sliceSpace = 3f
        pieDataSet.iconsOffset = MPPointF(0f, 40f)
        pieDataSet.selectionShift = 5f
        pieDataSet.colors = pieColors

        val pieData = PieData(pieDataSet)

        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(15f)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
        pieData.setValueTextColor(resources.getColor(R.color.white))

        overviewPieChart.data = pieData
        overviewPieChart.highlightValues(null)
        overviewPieChart.invalidate()
    }

    /**
     * Finds color based on sector name
     */
    private fun findColorResource(sector: String): Int {
        return when (sector) {
            "Transportation" -> resources.getColor(R.color.finance_sector)
            "Housing" -> resources.getColor(R.color.technology_sector)
            "Miscellaneous" -> resources.getColor(R.color.communication_sector)
            "Recreation" -> resources.getColor(R.color.industrial_sector)
            "Personal" -> resources.getColor(R.color.energy_sector)
            "Savings" -> resources.getColor(R.color.utility_sector)
            "Medical" -> resources.getColor(R.color.real_estate_sector)
            "Insurance" -> resources.getColor(R.color.material_sector)
            "Utilities" -> resources.getColor(R.color.healthcare_sector)
            "Food" -> resources.getColor(R.color.consumer_sector)
            else -> 0
        }
    }

}