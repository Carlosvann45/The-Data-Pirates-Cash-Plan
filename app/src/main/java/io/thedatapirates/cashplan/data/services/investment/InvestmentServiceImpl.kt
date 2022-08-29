package io.thedatapirates.cashplan.data.services.investment

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.thedatapirates.cashplan.BuildConfig
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentRequest
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockResponse
import io.thedatapirates.cashplan.data.dtos.investment.TotalInvestment
import kotlinx.serialization.json.Json

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

    override suspend fun createInvestment(investment: InvestmentRequest, accessToken: String?): InvestmentResponse {
        return client.post {
            url(HttpRoutes.INVESTMENT)
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = investment
        }
    }
}