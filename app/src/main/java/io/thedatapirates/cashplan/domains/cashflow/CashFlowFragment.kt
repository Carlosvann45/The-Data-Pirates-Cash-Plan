package io.thedatapirates.cashplan.domains.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_cash_flow.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [CashFlowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CashFlowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cash_flow, container, false)

        view.tvCashFlowText.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navNestedCashFlow)
        }

        return view
    }

}