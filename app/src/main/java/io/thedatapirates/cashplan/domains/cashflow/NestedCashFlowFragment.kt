package io.thedatapirates.cashplan.domains.cashflow

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.view.*
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import io.thedatapirates.cashplan.data.dtos.cashflow.CreateCashFlowItem
import io.thedatapirates.cashplan.data.dtos.cashflow.Deposit
import io.thedatapirates.cashplan.data.dtos.createAccount.CreateAccountRequest
import io.thedatapirates.cashplan.data.services.cashflow.CashFlowService
import kotlinx.android.synthetic.main.fragment_cash_flow.*
import kotlinx.android.synthetic.main.fragment_cashflow.view.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.view.*
import kotlinx.coroutines.*
import java.nio.channels.Selector
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * Use the [NestedCashFlowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class NestedCashFlowFragment : Fragment() {

    var option = ""
    private val cashFlowService = CashFlowServiceLocater.getCashFlowService()
    private lateinit var cashFlowContext: Context
    var cashFlowID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nested_cash_flow, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashFlowContext = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {

        super.onViewCreated(itemView, savedInstanceState)

        val spinner = itemView.sCashFlowSpinner
        val options = resources.getStringArray(R.array.cash_flow_choices_array)

        if (spinner != null) {
            val adapter = ArrayAdapter(
                itemView.context,
                R.layout.spinner_item, options
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    option = options[position]
                    Toast.makeText(
                        view.context,
                        getString(R.string.selected_item) + " " +
                                "" + options[position], Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        btnCancelItem.setOnClickListener {
            Navigation.findNavController(itemView).navigate(R.id.navCashFlow)
        }


        btnAddItem.setOnClickListener {
            if (etAddItemAmount.text.toString() != ""  && etAddItemName.text.toString() != "") {
                val itemName = etAddItemName.text.toString()
                val itemAmount = etAddItemAmount.text.toString().toFloat()

                val cashFlowItem = CreateCashFlowItem(itemName, "2022-09-25 12:00", 1)
                val deposit = Deposit(itemAmount)

                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {

                        createCashFlowItem(cashFlowItem)
                        createDepositItem(deposit)
                    }

                }

                Navigation.findNavController(itemView).navigate(R.id.rlCashFlowFragment)
            }
        }
    }

    private suspend fun createCashFlowItem(cashFlowItem: CreateCashFlowItem) {
        val sharedPreferences = cashFlowContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            cashFlowID = cashFlowService.createCashFlow(cashFlowItem, accessToken).id

        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    private suspend fun createDepositItem(deposit: Deposit) {
        val sharedPreferences = cashFlowContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            cashFlowService.createDepositForCashFlow(deposit, cashFlowID, accessToken)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}


