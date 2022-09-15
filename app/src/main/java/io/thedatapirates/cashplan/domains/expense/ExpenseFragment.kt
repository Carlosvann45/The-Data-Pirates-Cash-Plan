package io.thedatapirates.cashplan.domains.expense

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.services.category.CategoryService
import io.thedatapirates.cashplan.data.services.expense.ExpenseService
import io.thedatapirates.cashplan.domains.home.HomeServiceLocator
import io.thedatapirates.cashplan.domains.investment.InvestmentItemsAdapter
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.fragment_expense.view.*
import kotlinx.android.synthetic.main.fragment_investment.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject customer service into login fragment
 */
object ExpenseServiceLocator {
    fun getExpenseService(): ExpenseService = ExpenseService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class ExpenseFragment : Fragment() {

    private lateinit var expenseContext: Context
    private lateinit var progressOverlay: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenses: MutableList<ExpenseResponse>
    private val expenseService = HomeServiceLocator.getExpenseService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        GlobalScope.launch(Dispatchers.IO) {

            expenses = getExpensesForCustomer()

            expenses.add(ExpenseResponse())

            withContext(Dispatchers.Main) {

                recyclerView = view.rvExpenseItems
                recyclerView.layoutManager = LinearLayoutManager(expenseContext)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = ExpenseItemsAdapter(
                    expenses,
                    view
                )

                AndroidUtils.animateView(progressOverlay, View.GONE, 0.75f, 200L)
            }

        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        expenseContext = context
    }

    /**
     * Makes call to API to get all expenses for a customer
     */
    private suspend fun getExpensesForCustomer(): MutableList<ExpenseResponse> {
        val sharedPreferences =
            expenseContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")

        return try {
            expenseService.getExpensesForCustomer(accessToken)
        } catch (e: Exception) {
            print("Error: ${e.message}")
            mutableListOf()
        }
    }

}