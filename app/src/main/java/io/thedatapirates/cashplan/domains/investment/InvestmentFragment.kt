package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockData
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import kotlinx.android.synthetic.main.fragment_investment.view.*
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random

/**
 * Service locator to inject customer service into login fragment
 */
object InvestmentServiceLocator {
    fun getInvestmentService(): InvestmentService = InvestmentService.create()
}

/**
 * A simple [Fragment] subclass.
 * Use the [InvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@DelicateCoroutinesApi
class InvestmentFragment : Fragment() {

    private lateinit var investmentContext: Context
    private lateinit var recyclerView: RecyclerView
    private val investmentService = InvestmentServiceLocator.getInvestmentService()
    private var investmentsMap = mutableMapOf<String, TotalInvestment>()
    private val stockTypes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment, container, false)

        GlobalScope.launch(Dispatchers.IO) {

            val investments = getInvestmentInformation()

            // adds all the uniques investment types to a list
            investments.forEach { if (!stockTypes.contains(it.name)) stockTypes.add(it.name) }

            val stockDataList = getStockPriceData()

            for (investment in investments) {

                if (investmentsMap.containsKey(investment.name)) {
                    // adds investment information to existing total investment information in the hashmap
                    investmentsMap[investment.name] = investmentsMap[investment.name]!!.combineTotalInvestment(investment, stockDataList)
                } else {
                    // creates a new total investment from the current investment
                    investmentsMap[investment.name] = TotalInvestment().combineTotalInvestment(investment, stockDataList)
                }
            }

            var investmentOverview = TotalInvestment()

            for (key in investmentsMap.keys) {
                // adds current profit/loss information to investmennt
                val newInvestment = investmentsMap[key]!!.calculateCurrentProfitLoss()

                // adds information investment overview
                investmentOverview = investmentOverview.getTotalOverview(newInvestment)

                // updates information in the hashmap
                investmentsMap[key] = newInvestment
            }

            withContext(Dispatchers.Main) {

                val pieEntries = ArrayList<PieEntry>()

                val colors = ArrayList<Int>()

                var colorI = 0
                // sets up pie entries and colors for pie chart
                for(key in investmentsMap.keys) {
                    pieEntries.add(PieEntry((investmentOverview.shares / investmentsMap[key]!!.shares).toFloat()))

                    var newColor: Int

                    when (colorI) {
                        0 -> {
                            ++colorI
                            newColor = Color.BLACK
                            colors.add(newColor)
                        }
                        1 -> {
                            ++colorI
                            newColor = Color.LTGRAY
                            colors.add(newColor)
                        }
                        2 -> {
                            ++colorI
                            newColor = Color.GRAY
                            colors.add(newColor)
                        }
                        else -> {
                            ++colorI
                            newColor = Color.DKGRAY
                            colors.add(newColor)
                        }
                    }

                    investmentsMap[key]!!.color = newColor
                }

                val investmentItems = mutableListOf<TotalInvestment>()

                investmentItems.add(TotalInvestment())
                investmentItems.addAll(investmentsMap.values)
                investmentItems.add(TotalInvestment())

                // sets up recycler view and creates/adds each stock total investment to recycler
                recyclerView = view.rvInvestmentItems
                recyclerView.layoutManager = LinearLayoutManager(investmentContext)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = InvestmentItemsAdapter(
                    investmentItems,
                    investmentOverview,
                    pieEntries,
                    colors,
                    resources
                )
            }
        }

        return view
    }

    /**
     * When context attaches to fragment sets context to private variable
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        investmentContext = context
    }

    /**
     * Makes call to api to retrieve customer information
     */
    private suspend fun getInvestmentInformation(): MutableList<InvestmentResponse> {
        val sharedPreferences = investmentContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        try {
            return investmentService.getCustomerInvestments(accessToken)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return mutableListOf()
    }


    /**
     * gets all stock data information from the current list of stock types
     */
    private suspend fun getStockPriceData(): MutableList<StockData>? {
        val stockDataList = mutableListOf<StockData>()

        try {
            for (stockName in stockTypes) {
                stockDataList.add(investmentService.getStockData(stockName).data[0])
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return stockDataList
    }

    /**
     * Takes an investment response and stock data list to create a new investment
     * based on real time information
     */
    private fun TotalInvestment.combineTotalInvestment(
        newInvestment: InvestmentResponse, stockDataList: MutableList<StockData>?
    ): TotalInvestment {
        val currentPrice = stockDataList?.find { it.ticker == newInvestment.name }?.price ?: 0.00

        this.name = newInvestment.name
        this.shares += newInvestment.amount
        this.buyPrice += newInvestment.buyPrice

        this.totalAmount += (newInvestment.amount * newInvestment.buyPrice)
        this.currentAmount += (newInvestment.amount * currentPrice)
        this.currentPrice = currentPrice

        return this
    }

    /**
     * Adds both total investment to get an overview of the to investments together
     */
    private fun TotalInvestment.getTotalOverview(investment: TotalInvestment): TotalInvestment {
        this.name = investment.name
        this.shares += investment.shares
        this.buyPrice += investment.buyPrice

        this.totalAmount += investment.totalAmount
        this.currentAmount += investment.currentAmount
        this.currentP_L += investment.currentP_L
        this.currentP_L_Percent += investment.currentP_L_Percent

        return this
    }

    /**
     * Calculates the current profit/loss
     * of given total investment
     */
    private fun TotalInvestment.calculateCurrentProfitLoss(): TotalInvestment {

        this.currentP_L = (this.currentPrice * this.shares) - this.totalAmount
        this.currentP_L_Percent = (this.currentP_L / this.totalAmount) * 100

        return this
    }
}


