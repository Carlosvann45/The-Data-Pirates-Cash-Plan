package io.thedatapirates.cashplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.custom_picker.view.*
import kotlinx.android.synthetic.main.fragment_sell_investment.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [SellInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellInvestmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sell_investment, container, false)

        view.btnDialogButton.setOnClickListener {
            view.clCustomPickerLayout.visibility = View.VISIBLE
        }

        return view
    }

}