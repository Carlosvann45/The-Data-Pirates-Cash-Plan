package io.thedatapirates.cashplan.domains.investment

import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import kotlinx.android.synthetic.main.create_button.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * An adapter class to modify the recycler view
 */
class InvestmentItemsAdapter(
    private val totalInvestments: MutableList<TotalInvestment>,
    private val investmentOverview: TotalInvestment,
    private val pieEntries: ArrayList<PieEntry>,
    private val pieColors: ArrayList<Int>,
    private val resource: Resources
    ) : RecyclerView.Adapter<InvestmentItemsAdapter.InvestmentItemsViewHolder>() {

    /**
     * Inflates the a layout to add it to recycler view
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvestmentItemsAdapter.InvestmentItemsViewHolder {

        val itemView = when (viewType) {
            R.layout.create_button -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.create_button, parent, false)
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
                holder.itemView.btnCreateBtn.setOnClickListener {  }
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
                R.layout.create_button
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
    class InvestmentItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private fun fillInvestmentOverView(
        holder: InvestmentItemsAdapter.InvestmentItemsViewHolder
    ) {
        val overviewInvestmentTotalAmount: TextView =
            holder.itemView.findViewById(R.id.tvInvestmentTotalAmount)
        val overviewCurrentPL: TextView = holder.itemView.findViewById(R.id.tvInvestmentCurrentP_L)
        val overviewPieChart: PieChart =
            holder.itemView.findViewById(R.id.pcInvestmentItemsPieChart)

        val formatter = DecimalFormat("#,###.##")

        formatter.roundingMode = RoundingMode.DOWN

        // formats total amount
        overviewInvestmentTotalAmount.text =
            "$${formatter.format(investmentOverview.currentAmount)}"

        if (investmentOverview.currentP_L >= 0.00) {
            // formats code for total overview if it is positive
            overviewCurrentPL.text = "+${formatter.format(investmentOverview.currentP_L)}(+${
                formatter.format(investmentOverview.currentP_L_Percent)
            }%)"
        } else {
            // formats code for total overview if it is negative
            overviewCurrentPL.text = "${formatter.format(investmentOverview.currentP_L)}(${
                formatter.format(investmentOverview.currentP_L_Percent)
            }%)"
            overviewCurrentPL.setTextColor(resource.getColor(R.color.red))
        }

        //sets up the pie chart

        overviewPieChart.setUsePercentValues(true)
        overviewPieChart.description.isEnabled = false
        overviewPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        overviewPieChart.dragDecelerationFrictionCoef = 0.5f
        overviewPieChart.isDrawHoleEnabled = true
        overviewPieChart.setHoleColor(resource.getColor(R.color.blue))
        overviewPieChart.setTransparentCircleColor(resource.getColor(R.color.black))
        overviewPieChart.setTransparentCircleAlpha(110)
        overviewPieChart.holeRadius = 58f
        overviewPieChart.transparentCircleRadius = 61f
        overviewPieChart.setDrawCenterText(false)
        overviewPieChart.rotationAngle = 0f
        overviewPieChart.isRotationEnabled = true
        overviewPieChart.isHighlightPerTapEnabled = true
        overviewPieChart.animateY(1400, Easing.EaseInOutQuad)
        overviewPieChart.legend.isEnabled = false
        overviewPieChart.setEntryLabelColor(resource.getColor(R.color.white))
        overviewPieChart.setEntryLabelTextSize(12f)


        val pieDataSet = PieDataSet(pieEntries, "Mobile OS")

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
        val itemPieLegend: ImageView = holder.itemView.findViewById(R.id.tvInvestmentItemDiagramCircle)
        val itemStockName: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemName)
        val itemTotalAmount: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemTotalAmount)
        val itemTotalShares: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemTotalShares)
        val itemCurrentPrice: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemCurrentPrice)
        val itemCurrentPL: TextView = holder.itemView.findViewById(R.id.tvInvestmentItemCurrentP_L)

        val formatter = DecimalFormat("#,###.##")
        val currentItem = totalInvestments[position]

        formatter.roundingMode = RoundingMode.DOWN

        var imageDrawable = itemPieLegend.background
        imageDrawable = DrawableCompat.wrap(imageDrawable)
        DrawableCompat.setTint(imageDrawable, currentItem.color)
        itemPieLegend.background = imageDrawable

        itemStockName.text = currentItem.name
        itemTotalAmount.text = "$${formatter.format(currentItem.currentAmount)}"
        itemTotalShares.text = formatter.format(currentItem.shares)
        itemCurrentPrice.text = "$${formatter.format(currentItem.currentPrice)}"

        if (currentItem.currentP_L >= 0.00) {
            itemCurrentPL.text = "+${formatter.format(currentItem.currentP_L)}(+${formatter.format(currentItem.currentP_L_Percent)}%)"
        } else {
            itemCurrentPL.text = "${formatter.format(currentItem.currentP_L)}(${formatter.format(currentItem.currentP_L_Percent)}%)"
            itemCurrentPL.setTextColor(resource.getColor(R.color.red))
        }
    }
}