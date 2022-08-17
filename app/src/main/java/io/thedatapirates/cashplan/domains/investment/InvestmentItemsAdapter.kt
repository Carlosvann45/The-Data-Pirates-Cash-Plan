package io.thedatapirates.cashplan.domains.investment

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import java.math.RoundingMode
import java.text.DecimalFormat

class InvestmentItemsAdapter(
    private val totalInvestments: MutableList<TotalInvestment>,
    val resource: Resources
    ) : RecyclerView.Adapter<InvestmentItemsAdapter.InvestmentItemsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvestmentItemsAdapter.InvestmentItemsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.investment_item, parent, false)
        return InvestmentItemsViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: InvestmentItemsAdapter.InvestmentItemsViewHolder,
        position: Int
    ) {
        val formatter = DecimalFormat("#,###.##")
        val currentItem = totalInvestments[position]

        formatter.roundingMode = RoundingMode.DOWN
        holder.itemStockName.text = currentItem.name
        holder.itemTotalAmount.text = "$${formatter.format(currentItem.amount)}"
        holder.itemTotalShares.text = formatter.format(currentItem.shares)
        holder.itemCurrentPrice.text = "$${formatter.format(currentItem.currentPrice)}"

        if (currentItem.currentP_L >= 0.00) {
            holder.itemCurrentP_L.text = "+${formatter.format(currentItem.currentP_L)}(+${formatter.format(currentItem.currentP_L_Percent)}%)"
        } else {
            holder.itemCurrentP_L.text = "${formatter.format(currentItem.currentP_L)}(${formatter.format(currentItem.currentP_L_Percent)}%)"
            holder.itemCurrentP_L.setTextColor(resource.getColor(R.color.red))
        }
    }

    override fun getItemCount(): Int {
        return totalInvestments.size
    }

    class InvestmentItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemStockName = itemView.findViewById<TextView>(R.id.tvInvestmentItemName)
        val itemTotalAmount = itemView.findViewById<TextView>(R.id.tvInvestmentItemTotalAmount)
        val itemTotalShares = itemView.findViewById<TextView>(R.id.tvInvestmentItemTotalShares)
        val itemCurrentPrice = itemView.findViewById<TextView>(R.id.tvInvestmentItemCurrentPrice)
        val itemCurrentP_L = itemView.findViewById<TextView>(R.id.tvInvestmentItemCurrentP_L)
    }
}