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
import io.thedatapirates.cashplan.data.dtos.cashflow.Deposit
import io.thedatapirates.cashplan.data.dtos.cashflow.Frequency
import io.thedatapirates.cashplan.data.services.cashflow.CashFlowService
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.domains.investment.InvestmentServiceLocator
import kotlinx.android.synthetic.main.fragment_cash_flow.*
import kotlinx.android.synthetic.main.fragment_cash_flow.rvExpenses
import kotlinx.android.synthetic.main.fragment_cash_flow_list.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.*
import kotlinx.coroutines.*


@DelicateCoroutinesApi
class CashFlowItemFragment : Fragment() {
    private val cashFlowService = CashFlowServiceLocater.getCashFlowService()
    private lateinit var cashFlowContext: Context
    private lateinit var cashFlowItemAdapter: CashFlowItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cashFlowItemAdapter = CashFlowItemAdapter(CashFlowItemsResponse(name="", id=0, deposits = mutableListOf(), frequency = Frequency(dateCreated = "", dateUpdated = "", id = 0, name = "")))

        return inflater.inflate(R.layout.fragment_cash_flow_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashFlowContext = context
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val depositItem = arguments?.getInt("position")
                if (depositItem != null) {
                    cashFlowItemAdapter = CashFlowItemAdapter(getCashFlowInformation()[depositItem])
                }

                rvDepositsList.apply {
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = cashFlowItemAdapter

                }
                tvDepositTotal.text = cashFlowItemAdapter.depositsTotal().toString()
            }
        }
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