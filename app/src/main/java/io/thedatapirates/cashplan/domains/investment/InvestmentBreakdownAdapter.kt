package io.thedatapirates.cashplan.domains.investment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

class InvestmentBreakdownAdapter(
    private val investmentTransactions: MutableList<InvestmentResponse>
): RecyclerView.Adapter<InvestmentBreakdownAdapter.InvestmentBreakdownViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvestmentBreakdownViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.investment_breakdown_item, parent, false)
        return InvestmentBreakdownViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvestmentBreakdownViewHolder, position: Int) {
        val currentItem = investmentTransactions[position]
        val itemDate: TextView = holder.itemView.findViewById(R.id.tvInvestmentBreakdownDate)
        val itemShares: TextView = holder.itemView.findViewById(R.id.tvInvestmentBreakdownShares)
        val itemPrice: TextView = holder.itemView.findViewById(R.id.tvInvestmentBreakdownAmount)
        val formatter = SimpleDateFormat("MM-dd-yy", Locale.US)

        itemDate.text = currentItem.dateCreated.subSequence(0, currentItem.dateCreated.indexOfFirst { it == 'T' })
        itemShares.text = String.format("%,.2f", currentItem.amount)
        itemPrice.text = "$${String.format("%,.2f", currentItem.buyPrice)}"

    }

    override fun getItemCount(): Int {
       return investmentTransactions.size
    }

    class InvestmentBreakdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}