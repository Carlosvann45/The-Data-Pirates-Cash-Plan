package io.thedatapirates.cashplan.domains.investment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.features.*
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockData
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import io.thedatapirates.cashplan.data.services.customer.CustomerService
import io.thedatapirates.cashplan.data.services.investment.InvestmentService
import io.thedatapirates.cashplan.domains.login.CustomerServiceLocator
import io.thedatapirates.cashplan.domains.login.LoginServiceLocator
import kotlinx.android.synthetic.main.fragment_investment.view.*
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat

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
    private var investmentsMap = mutableMapOf<String, MutableList<InvestmentResponse>>()
    private var totalInvestments = mutableListOf<TotalInvestment>()
    private val stockTypes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment, container, false)

        GlobalScope.launch(Dispatchers.IO) {

            val investments = getInvestmentInformation()

            for (investment in investments) {
                if (investmentsMap.containsKey(investment.name)) {
                    val oldInvestments = investmentsMap[investment.name]

                    oldInvestments!!.add(investment)

                    investmentsMap[investment.name] = oldInvestments
                } else {
                    val newInvestments = mutableListOf<InvestmentResponse>()

                    newInvestments.add(investment)

                    stockTypes.add(investment.name)

                    investmentsMap[investment.name] = newInvestments
                }
            }

            val stockDataList = async{ getStockPriceData() }

            for (key in investmentsMap.keys) {
                addTotalInvestment(investmentsMap[key]!!, stockDataList.await())
            }

            withContext(Dispatchers.Main) {
                val totalOverview = getTotalOverview(investments)

                val formatter = DecimalFormat("#,###.##")

                formatter.roundingMode = RoundingMode.DOWN
                view.tvInvestmentTotalAmount.text = "$${formatter.format(totalOverview.amount)}"

                if (totalOverview.currentP_L >= 0.00) {
                    view.tvInvestmentCurrentP_L.text = "+${formatter.format(totalOverview.currentP_L)}(+${formatter.format(totalOverview.currentP_L_Percent)}%)"
                } else {
                    view.tvInvestmentCurrentP_L.text = "${formatter.format(totalOverview.currentP_L)}(${formatter.format(totalOverview.currentP_L_Percent)}%)"
                    view.tvInvestmentCurrentP_L.setTextColor(resources.getColor(R.color.red))
                }

                recyclerView = view.rvInvestmentItems
                recyclerView.layoutManager = LinearLayoutManager(investmentContext)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = InvestmentItemsAdapter(totalInvestments, resources)

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

    private fun addTotalInvestment(investments: MutableList<InvestmentResponse>, stockDataList: MutableList<StockData>?) {
        val totalInvestment = TotalInvestment()
        val currentPrice = stockDataList?.find { it.ticker == investments[0].name }?.price ?: 0.00
        var totalAmount = 0.00;
        var currentAmount = 0.00;


        for (investment in investments) {
            totalInvestment.name = investment.name
            totalInvestment.shares += investment.amount
            totalInvestment.buyPrice += investment.buyPrice

            totalAmount += (investment.amount * investment.buyPrice)
            currentAmount += (investment.amount * currentPrice)
        }

        totalInvestment.amount = currentAmount
        totalInvestment.currentPrice = currentPrice
        totalInvestment.currentP_L = (totalInvestment.currentPrice * totalInvestment.shares) - totalAmount
        totalInvestment.currentP_L_Percent = (totalInvestment.currentP_L / totalAmount) * 100

        totalInvestments.add(totalInvestment)
    }

    private fun getTotalOverview(investments: MutableList<InvestmentResponse>): TotalInvestment {
        val investmentOverview = TotalInvestment()
        var totalAmount = 0.00;

        for (investment in investments) {
            investmentOverview.name = investment.name
            investmentOverview.shares += investment.amount
            investmentOverview.buyPrice += investment.buyPrice

            totalAmount += (investment.amount * investment.buyPrice)
        }

        var currentAmount = 0.00

        for (investment in totalInvestments) {
            currentAmount += investment.amount
        }

        investmentOverview.amount = totalAmount
        investmentOverview.currentP_L = currentAmount - totalAmount
        investmentOverview.currentP_L_Percent = ((investmentOverview.currentP_L / totalAmount) * 100)

        return investmentOverview
    }
}