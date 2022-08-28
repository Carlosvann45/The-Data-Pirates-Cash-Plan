package io.thedatapirates.cashplan.domains.cashflow

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
import kotlinx.android.synthetic.main.fragment_cash_flow.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.*


class CashFlowFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private lateinit var cashFlowAdapter: CashFlowAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cash_flow, container, false)
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        cashFlowAdapter = CashFlowAdapter(mutableListOf())

        setFragmentResultListener("requestKey") { key, bundle ->
            // Any type can be passed via to the bundle
            val itemName = bundle.getString("name")
            val itemOption = bundle.getString("option")
            val itemAmount = bundle.getFloat("amount")

            if (itemOption != null  && itemName != null) {
                val item = CashFlowItem(itemOption, itemName, itemAmount)
                cashFlowAdapter.addExpense(item)
            }
        }

        super.onViewCreated(itemView, savedInstanceState)
        rvExpenses.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = cashFlowAdapter

        }
        tvExpensesTotal.text = cashFlowAdapter.expensesTotal().toString()

        btnAddExpense.setOnClickListener {
            Navigation.findNavController(itemView).navigate(R.id.rlNestedCashFlowFragment)
        }

        btnDeleteDoneExpenses.setOnClickListener {
            cashFlowAdapter.deleteItems()
            tvExpensesTotal.text = cashFlowAdapter.expensesTotal().toString()
        }
    }
}