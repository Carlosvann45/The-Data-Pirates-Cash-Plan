package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieEntry
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockResponse
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.utils.AndroidUtils
import kotlinx.android.synthetic.main.fragment_investment.view.*
import kotlinx.android.synthetic.main.progress_spinner_overlay.view.*
import kotlinx.coroutines.*

/**
 * Service locator to inject customer service into investment fragment
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
    private lateinit var progressOverlay: View
    private val investmentService = InvestmentServiceLocator.getInvestmentService()
    private var investments = mutableListOf<InvestmentResponse>()
    private var investmentsMap = mutableMapOf<String, TotalInvestment>()
    private val stockTypes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment, container, false)

        progressOverlay = view.clProgressSpinnerOverlay

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.75f, 200L)

        GlobalScope.launch(Dispatchers.IO) {

            investments = getInvestmentInformation()

            // adds all the uniques investment types to a list
            investments.forEach { if (!stockTypes.contains(it.name)) stockTypes.add(it.name) }

            val stockDataList = getStockPriceData()

            val investmentOverview = TotalInvestment()

            for (stock in stockTypes) {
                // gets all investments by current stock type
                val uniqueInvestments = investments.filter { it.name == stock }

                val totalInvestment = TotalInvestment()

                // calculates the total for each stock type
                uniqueInvestments.forEach {
                    totalInvestment.calculateTotalInvestment(it, stockDataList)
                }

                // adds the current profit/loss
                totalInvestment.calculateCurrentProfitLoss()

                if (totalInvestment.shares > 0) {
                    // adds the stock total to the investment overview
                    investmentOverview.calculateTotalOverview(totalInvestment)

                    if (totalInvestment.currentAmount > 0 || totalInvestment.totalAmount > 0) {
                        // adds total investment to map
                        investmentsMap[stock] = totalInvestment
                    }
                }
            }

            // calculates the total for each sector
            val techOverview = TotalInvestment().getSectorOverview("Technology")
            val financeOverView = TotalInvestment().getSectorOverview("Finance")
            val consumerOverview = TotalInvestment().getSectorOverview("Consumer")
            val healthcareOverview = TotalInvestment().getSectorOverview("Healthcare")
            val materialOverview = TotalInvestment().getSectorOverview("Material")
            val realEstateOverview = TotalInvestment().getSectorOverview("Real Estate")
            val utilityOverview = TotalInvestment().getSectorOverview("Utility")
            val energyOverview = TotalInvestment().getSectorOverview("Energy")
            val industrialOverview = TotalInvestment().getSectorOverview("Industrial")
            val communication = TotalInvestment().getSectorOverview("Communication")

            withContext(Dispatchers.Main) {

                val pieEntries = ArrayList<PieEntry>()

                val colors = ArrayList<Int>()

                // sets up pie entries and colors for pie chart getting the average based
                // on the investment overview total and the sector total
                if (techOverview.currentAmount > 0 || techOverview.totalAmount > 0) {
                    colors.add(findColorResource("Technology"))
                    pieEntries.add(PieEntry(((techOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (financeOverView.currentAmount > 0 || financeOverView.totalAmount > 0) {
                    colors.add(findColorResource("Finance"))
                    pieEntries.add(PieEntry(((financeOverView.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (consumerOverview.currentAmount > 0 || consumerOverview.totalAmount > 0) {
                    colors.add(findColorResource("Consumer"))
                    pieEntries.add(PieEntry(((consumerOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (healthcareOverview.currentAmount > 0 || healthcareOverview.totalAmount > 0) {
                    colors.add(findColorResource("Healthcare"))
                    pieEntries.add(PieEntry(((healthcareOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (materialOverview.currentAmount > 0 || materialOverview.totalAmount > 0) {
                    colors.add(findColorResource("Material"))
                    pieEntries.add(PieEntry(((materialOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (realEstateOverview.currentAmount > 0 || realEstateOverview.totalAmount > 0) {
                    colors.add(findColorResource("Real Estate"))
                    pieEntries.add(PieEntry(((realEstateOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (utilityOverview.currentAmount > 0 || utilityOverview.totalAmount > 0) {
                    colors.add(findColorResource("Utility"))
                    pieEntries.add(PieEntry(((utilityOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (energyOverview.currentAmount > 0 || energyOverview.totalAmount > 0) {
                    colors.add(findColorResource("Energy"))
                    pieEntries.add(PieEntry(((energyOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (industrialOverview.currentAmount > 0 || industrialOverview.totalAmount > 0) {
                    colors.add(findColorResource("Industrial"))
                    pieEntries.add(PieEntry(((industrialOverview.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                if (communication.currentAmount > 0 || communication.totalAmount > 0) {
                    colors.add(findColorResource("Communication"))
                    pieEntries.add(PieEntry(((communication.currentAmount / investmentOverview.currentAmount) * 100).toFloat()))
                }

                val investmentItems = mutableListOf<TotalInvestment>()

                // ensures the overview will get added to the recycler
                investmentItems.add(TotalInvestment())

                // all of the individual stocks get added
                investmentItems.addAll(investmentsMap.values)

                // ensures the buy and sell buttons get added to the recycler view at the bottom
                investmentItems.add(TotalInvestment())

                // sets up recycler view and creates/adds each stock total investment to recycler
                recyclerView = view.rvInvestmentItems
                recyclerView.layoutManager = LinearLayoutManager(investmentContext)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = InvestmentItemsAdapter(
                    investmentItems,
                    investments,
                    investmentOverview,
                    pieEntries,
                    colors,
                    resources,
                    view
                )

                AndroidUtils.animateView(progressOverlay, View.GONE, 0f, 200L)
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
        val sharedPreferences =
            investmentContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
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
    private suspend fun getStockPriceData(): MutableList<StockResponse>? {
        var stockResponses = mutableListOf<StockResponse>()

        try {
            val newStockResponses = investmentService.getStockData(stockTypes.joinToString(","))

            stockResponses = newStockResponses
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return stockResponses
    }

    /**
     * Takes an investment response and stock data list to create a new investment
     * based on real time information
     */
    private fun TotalInvestment.calculateTotalInvestment(
        newInvestment: InvestmentResponse, stockDataList: MutableList<StockResponse>?
    ): TotalInvestment {
        val currentPrice =
            stockDataList?.find { it.symbol == newInvestment.name }?.price ?: newInvestment.buyPrice

        this.name = newInvestment.name
        this.sector = newInvestment.sector
        this.color = findColorResource(newInvestment.sector)

        // calculates shares and current amount depending of if the transaction was a sell or buy
        if (newInvestment.buyPrice > 0) {
            this.shares += newInvestment.amount
            this.currentAmount += (newInvestment.amount * currentPrice)
        } else {
            this.shares -= newInvestment.amount
            this.currentAmount -= (newInvestment.amount * currentPrice)
        }

        this.buyPrice += newInvestment.buyPrice
        this.totalAmount += (newInvestment.amount * newInvestment.buyPrice)
        this.currentPrice = currentPrice

        return this
    }

    /**
     * Adds both total investment to get an overview of the to investments together
     */
    private fun TotalInvestment.calculateTotalOverview(investment: TotalInvestment): TotalInvestment {
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

        val numOfInvestments = investments.count { it.name == this.name && it.buyPrice > 0 }

        this.buyPrice = this.buyPrice / numOfInvestments
        this.currentP_L = (this.currentPrice * this.shares) - this.totalAmount
        this.currentP_L_Percent = (this.currentP_L / this.totalAmount) * 100

        return this
    }

    /**
     * Finds color based on sector name
     */
    private fun findColorResource(sector: String): Int {
        return when (sector) {
            "Finance" -> resources.getColor(R.color.finance_sector)
            "Technology" -> resources.getColor(R.color.technology_sector)
            "Communication" -> resources.getColor(R.color.communication_sector)
            "Industrial" -> resources.getColor(R.color.industrial_sector)
            "Energy" -> resources.getColor(R.color.energy_sector)
            "Utility" -> resources.getColor(R.color.utility_sector)
            "Real Estate" -> resources.getColor(R.color.real_estate_sector)
            "Material" -> resources.getColor(R.color.material_sector)
            "Healthcare" -> resources.getColor(R.color.healthcare_sector)
            "Consumer" -> resources.getColor(R.color.consumer_sector)
            else -> 0
        }
    }

    /**
     * calculates the amount per sector
     */
    private fun TotalInvestment.getSectorOverview(sector: String): TotalInvestment {
        investmentsMap.values.filter { it.sector == sector }.forEach {
            this.currentAmount += it.currentAmount
        }

        return this
    }
}


