package io.thedatapirates.cashplan.data.services.investment

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentRequest
import io.thedatapirates.cashplan.data.dtos.investment.InvestmentResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockResponse
import io.thedatapirates.cashplan.data.dtos.investment.StockTicker

/**
 * Interface method for investment api calls
 */
interface InvestmentService {

    suspend fun getCustomerInvestments(accessToken: String?): MutableList<InvestmentResponse>

    suspend fun getStockData(stockNames: String) : MutableList<StockResponse>

    suspend fun createInvestment(investment: InvestmentRequest, accessToken: String?): InvestmentResponse

    suspend fun getAllStockTickers(): MutableList<StockTicker>
    /**
     * Dependency injection for investment service
     */
    companion object {
        fun create(): InvestmentService {
            return InvestmentServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        accept(ContentType.Application.Json)
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        })
                    }
                }
            )
        }
    }
}