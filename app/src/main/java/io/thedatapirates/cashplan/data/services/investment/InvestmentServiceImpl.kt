package io.thedatapirates.cashplan.data.services.investment

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.BuildConfig
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockData
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

    override suspend fun getStockData(stockName: String): StockResponse {
        val url = "${HttpRoutes.STOCK_PRICES}$stockName&api_token=${BuildConfig.API_KEY}"
        return client.get {
            url(url)
        }
    }
}