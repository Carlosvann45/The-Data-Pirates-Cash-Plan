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

        helpItems.add(
            HelpItem("What is the Cash Flow page and how can I use it?",
                    "The Cash Flow page is for tracking all of your income. " +
                    "It will help you see how much your currently making on average per month. "
                )
        )

        helpItems.add(
            HelpItem("What is the Expense page and how can I use it?",
                "The Expense page is for tracking all of your payments and spending habits. " +
                        "It will help you see how much your currently spending, " +
                        "while also allowing you see where your spending your money."
            )
        )

        helpItems.add(
            HelpItem("What is the Investment page and how can I use it?",
                "The Investment page is for tracking all of your stocks through the stock market. " +
                        "It also uses real time data from Yahoo Finance to help you keep up to date with real time prices. " +
                        "The page also displays and calculates your current total and profit loss based on all of your stocks."
            )
        )

        helpItems.add(
            HelpItem("How can I share this app with my friends?",
                "We would love you to share this app! " +
                        "If you go to the side navigation on the bottom you can go to any one of out pages and share us there!"
            )
        )

        helpItems.add(
            HelpItem("where can I write a review?",
                "We would love you get your feedback on what we can approve! " +
                        "If you go to the side navigation in the middle you can see a option the will allow you to write a review."
            )
        )

        helpItems.add(
            HelpItem("what are stock markets?",
                        "Stock markets are a place where buyers and sellers meet to exchange equity shares of public corporations."
            )
        )

        helpItems.add(
            HelpItem("what is a stock?",
                        "A stock is a form of security that indicates the holder has proportionate ownership in the issuing corporation and is sold predominantly on stock exchanges. "+
                        "Corporations issue stock to raise funds to operate their businesses."
            )
        )

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