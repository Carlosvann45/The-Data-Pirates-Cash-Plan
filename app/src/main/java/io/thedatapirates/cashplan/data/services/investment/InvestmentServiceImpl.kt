package io.thedatapirates.cashplan.data.services.investment

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.BuildConfig
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockResponse

/**
 * Implementation of investment service class for api calls
 */
class InvestmentServiceImpl(
    private val client: HttpClient
) : InvestmentService {

    /**
     * Gets investment information related to customer from access token
     */
    override suspend fun getCustomerInvestments(accessToken: String?): MutableList<InvestmentResponse> {
        return client.get {
            url(HttpRoutes.INVESTMENT)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

    /**
     * Gets stock data with api key from account and stock name string for stocks to get
     */
    override suspend fun getStockData(stockNames: String): MutableList<StockResponse> {
        return client.get {
            url("${HttpRoutes.STOCK_PRICES}$stockNames")
        }
    }
}