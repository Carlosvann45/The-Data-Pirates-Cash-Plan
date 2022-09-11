package io.thedatapirates.cashplan.domains.cashflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_cash_flow.view.*
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.features.*
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import io.thedatapirates.cashplan.data.services.cashflow.CashFlowService
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.domains.investment.InvestmentServiceLocator
import io.thedatapirates.cashplan.domains.login.CustomerServiceLocator
import kotlinx.android.synthetic.main.fragment_cash_flow.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.*
import kotlinx.coroutines.*


object CashFlowServiceLocater {
    fun getCashFlowService(): CashFlowService = CashFlowService.create()
}

@DelicateCoroutinesApi
class CashFlowFragment : Fragment() {
    private val cashFlowService = CashFlowServiceLocater.getCashFlowService()
    private lateinit var cashFlowContext: Context
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private lateinit var cashFlowAdapter: CashFlowAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        cashFlowAdapter = CashFlowAdapter(mutableListOf())
        return inflater.inflate(R.layout.fragment_cash_flow, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashFlowContext = context
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {


        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                cashFlowAdapter = CashFlowAdapter(getCashFlowInformation())

                rvExpenses.apply {
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = cashFlowAdapter

                }
                tvExpensesTotal.text = cashFlowAdapter.expensesTotal().toString()
            }
        }

        btnAddExpense.setOnClickListener {
            Navigation.findNavController(itemView).navigate(R.id.rlNestedCashFlowFragment)
        }

//        btnDeleteDoneExpenses.setOnClickListener {
//
//            cashFlowAdapter.deleteItem(0)
//
//            tvExpensesTotal.text = cashFlowAdapter.expensesTotal().toString()
//        }
    }

    private suspend fun getCashFlowInformation(): MutableList<CashFlowItemsResponse> {
        val sharedPreferences = cashFlowContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            return cashFlowService.getCashFlow(accessToken)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return mutableListOf()
    }
}