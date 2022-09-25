package io.thedatapirates.cashplan.domains.investment

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import kotlinx.android.synthetic.main.investment_buttons.view.*
import java.text.DecimalFormat

/**
 * An adapter class to modify the recycler view
 */
class InvestmentItemsAdapter(
    private val totalInvestments: MutableList<TotalInvestment>,
    private val investments: MutableList<InvestmentResponse>,
    private val investmentOverview: TotalInvestment,
    private val pieEntries: ArrayList<PieEntry>,
    private val pieColors: ArrayList<Int>,
    private val resource: Resources,
    private val view: View
) : RecyclerView.Adapter<InvestmentItemsAdapter.InvestmentItemsViewHolder>() {

    /**
     * Inflates the a layout to add it to recycler view
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvestmentItemsAdapter.InvestmentItemsViewHolder {

        val itemView = when (viewType) {
            R.layout.investment_buttons -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.investment_buttons, parent, false)
            }
            R.layout.investment_overview -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.investment_overview, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.investment_item, parent, false)
            }
        }

        return InvestmentItemsViewHolder(itemView)
    }

    /**
     * Creates/adds each item to the recycler view
     */
    override fun onBindViewHolder(
        holder: InvestmentItemsAdapter.InvestmentItemsViewHolder,
        position: Int
    ) {
        when (position) {
            0 -> {
                fillInvestmentOverView(holder)
            }
            totalInvestments.size - 1 -> {
                holder.itemView.btnSellBtn.setOnClickListener {

                    if (totalInvestments.size > 2) {
                        val bundle = Bundle()
                        bundle.putString("stockList", Gson().toJson(totalInvestments))
                        Navigation.findNavController(view)
                            .navigate(R.id.llSellInvestmentFragment, bundle)
                    }
                }

                holder.itemView.btnBuyBtn.setOnClickListener {
                    Navigation.findNavController(view).navigate(R.id.llBuyInvestmentFragment)
                }
            }
            else -> {
                fillInvestmentItem(holder, position)
            }
        }
    }

    /**
     * Decides on what layout to return based on position
     */
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                R.layout.investment_overview
            }
            totalInvestments.size - 1 -> {
                R.layout.investment_buttons
            }
            else -> {
                R.layout.investment_item
            }
        }
    }

    /**
     * Tells recycler view how many items are in the array
     */
    override fun getItemCount(): Int {
        return totalInvestments.size
    }

    /**
     * Gets the different items from the view to modify each field on a given layout
     */
    class InvestmentItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun fillInvestmentOverView(
        holder: InvestmentItemsAdapter.InvestmentItemsViewHolder
    ) {
        val overviewInvestmentTotalAmount: TextView =
            holder.itemView.findViewById(R.id.tvInvestmentTotalAmount)
        val overviewCurrentPL: TextView = holder.itemView.findViewById(R.id.tvInvestmentCurrentP_L)
        val overviewPieChart: PieChart =
            holder.itemView.findViewById(R.id.pcInvestmentItemsPieChart)

        val formatter = DecimalFormat("#,###.##")

        // formats total amount
        overviewInvestmentTotalAmount.text =
            "$${String.format("%,.2f", investmentOverview.currentAmount)}"

        if (investmentOverview.currentP_L >= 0.00) {
            // formats code for total overview if it is positive
            overviewCurrentPL.text = "+${String.format("%,.2f", investmentOverview.currentP_L)}(+${
                String.format("%,.2f", investmentOverview.currentP_L_Percent)
            }%)"
        } else {
            // formats code for total overview if it is negative
            overviewCurrentPL.text = "${String.format("%,.2f", investmentOverview.currentP_L)}(${
                String.format("%,.2f", investmentOverview.currentP_L_Percent)
            }%)"
            overviewCurrentPL.setTextColor(resource.getColor(R.color.red))
        }

        //sets up the pie chart
        overviewPieChart.setUsePercentValues(true)
        overviewPieChart.minAngleForSlices = 15f

        if (totalInvestments.size > 2) {
            overviewPieChart.description.isEnabled = false
        } else {
            val description = Description()

            description.text = "No current stocks available"
            description.textColor = resource.getColor(R.color.white)
            description.textSize = 12f
            description.typeface = Typeface.DEFAULT_BOLD
            description.xOffset = 115f
            description.yOffset = 180f

            overviewPieChart.description.isEnabled = true
            overviewPieChart.description = description
        }

        overviewPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        overviewPieChart.dragDecelerationFrictionCoef = 0.5f
        overviewPieChart.isDrawHoleEnabled = true
        overviewPieChart.setHoleColor(resource.getColor(R.color.blue))
        overviewPieChart.setTransparentCircleColor(resource.getColor(R.color.black))
        overviewPieChart.setTransparentCircleAlpha(110)
        overviewPieChart.holeRadius = 65f
        overviewPieChart.transparentCircleRadius = 70f
        overviewPieChart.setDrawCenterText(false)
        overviewPieChart.rotationAngle = 0f
        overviewPieChart.isRotationEnabled = true
        overviewPieChart.isHighlightPerTapEnabled = true
        overviewPieChart.animateY(1400, Easing.EaseInOutQuad)
        overviewPieChart.legend.isEnabled = false


        val pieDataSet = PieDataSet(pieEntries, "")

        pieDataSet.setDrawIcons(false)
        pieDataSet.sliceSpace = 3f
        pieDataSet.iconsOffset = MPPointF(0f, 40f)
        pieDataSet.selectionShift = 5f
        pieDataSet.colors = pieColors

        val pieData = PieData(pieDataSet)

        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(15f)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
        pieData.setValueTextColor(resource.getColor(R.color.white))

        overviewPieChart.data = pieData
        overviewPieChart.highlightValues(null)
        overviewPieChart.invalidate()
    }

    private fun fillInvestmentItem(
        holder: InvestmentItemsAdapter.InvestmentItemsViewHolder, position: Int
    ) {
        val investmentItem: CardView = holder.itemView.findViewById(R.id.rlInvestmentItem)
        val itemPieLegend: ImageView =
            holder.itemView.findViewById(R.id.tvInvestmentItemDiagramCircle)
        val itemStockName: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemName)
        val itemTotalAmount: TextView =
            holder.itemView.findViewById(R.id.tvInvestmentItemTotalAmount)
        val itemTotalShares: TextView =
            holder.itemView.findViewById(R.id.tvInvestmentItemTotalShares)
        val itemCurrentPrice: TextView =
            holder.itemView.findViewById(R.id.tvInvestmentItemCurrentPrice)
        val itemCurrentPL: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemCurrentP_L)

        val currentItem = totalInvestments[position]


        var imageDrawable = itemPieLegend.background
        imageDrawable = DrawableCompat.wrap(imageDrawable)
        DrawableCompat.setTint(imageDrawable, currentItem.color)
        itemPieLegend.background = imageDrawable

        itemStockName.text = currentItem.name
        itemTotalAmount.text = "$${String.format("%,.2f", currentItem.currentAmount)}"
        itemTotalShares.text = String.format("%.2f", currentItem.shares)
        itemCurrentPrice.text = "$${String.format("%,.2f", currentItem.currentPrice)}"

        if (currentItem.currentP_L >= 0.00) {
            itemCurrentPL.text = "+${String.format("%,.2f", currentItem.currentP_L)}(+${
                String.format(
                    "%,.2f",
                    currentItem.currentP_L_Percent
                )
            }%)"
        } else {
            itemCurrentPL.text = "${String.format("%,.2f", currentItem.currentP_L)}(${
                String.format(
                    "%,.2f",
                    currentItem.currentP_L_Percent
                )
            }%)"
            itemCurrentPL.setTextColor(resource.getColor(R.color.red))
        }

        investmentItem.setOnClickListener {
            val investmentTransactions = investments.filter { it.name == currentItem.name }
            val bundle = Bundle()
            bundle.putString("investmentTransactions", Gson().toJson(investmentTransactions))
            bundle.putString("name", currentItem.name)

            Navigation.findNavController(view).navigate(R.id.llInvestmentBreakdownFragment, bundle)
        }
    }
}