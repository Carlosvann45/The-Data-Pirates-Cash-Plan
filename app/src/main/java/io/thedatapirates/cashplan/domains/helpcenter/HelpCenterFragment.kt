package io.thedatapirates.cashplan.domains.helpcenter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.helpCenter.HelpItem
import io.thedatapirates.cashplan.domains.investment.InvestmentItemsAdapter
import kotlinx.android.synthetic.main.fragment_help_center.view.*
import kotlinx.android.synthetic.main.fragment_investment.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [HelpCenterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HelpCenterFragment : Fragment() {

    private lateinit var helpCenterContext: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var helpItems: MutableList<HelpItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_help_center, container, false)

        helpItems = mutableListOf()

        helpItems.add(
            HelpItem("What is Ca\$h Plan and how can I use it?",
            "Ca\$h Plan is a Finance app that was created to help people better manager their money. " +
                    "Not only are you able to manage your income, but your expenses and bills as well. " +
                    "We also offer a way to track your stock investments and help manage you profits and loses. " +
                    "You can read through other questions if you have any more in depth questions."
        ))

        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))
        helpItems.add(HelpItem("test", "test"))


        recyclerView = view.rvHelpCenterItems
        recyclerView.layoutManager = LinearLayoutManager(helpCenterContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = HelpItemsAdapter(helpItems)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        helpCenterContext = context
    }
}