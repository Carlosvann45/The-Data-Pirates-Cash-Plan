package io.thedatapirates.cashplan.domains.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_cash_flow.*
import kotlinx.android.synthetic.main.fragment_cashflow.view.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.*
import kotlinx.android.synthetic.main.fragment_nested_cash_flow.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [NestedCashFlowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NestedCashFlowFragment : Fragment() {


    var option = ""
    private lateinit var cashFlowAdapter: CashFlowAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nested_cash_flow, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        cashFlowAdapter = CashFlowAdapter(mutableListOf())

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

            val itemName = etAddItemName.text.toString()
            val itemAmount = etAddItemAmount.text.toString().toFloat()
            val itemMonthly = cbItemMonthly.isChecked

            if (itemName.isNotEmpty()) {
                setFragmentResult(
                    "requestKey",
                    bundleOf(
                        "name" to itemName,
                        "amount" to itemAmount,
                        "option" to option,
                        "monthly" to itemMonthly
                    )
                )
            }
            Navigation.findNavController(itemView).navigate(R.id.rlCashFlowFragment)
        }
    }

}