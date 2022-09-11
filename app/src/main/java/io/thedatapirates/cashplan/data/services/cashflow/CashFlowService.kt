package io.thedatapirates.cashplan.data.services.cashflow

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import io.thedatapirates.cashplan.data.dtos.cashflow.CreateCashFlowItem
import io.thedatapirates.cashplan.data.dtos.cashflow.Deposit


/**
 * Interface method for customer api calls
 */
interface CashFlowService {

    suspend fun getCashFlow(accessToken: String?): MutableList<CashFlowItemsResponse>
    suspend fun createCashFlow(cashFlowInformation: CreateCashFlowItem, accessToken: String?): CashFlowItemsResponse
    suspend fun createDepositForCashFlow(amount: Float, id: Int, accessToken: String?)
//    suspend fun removeCashFlow(id: Int, accessToken: String?)
    /**
     * Dependency injection for customer service
     */
    companion object {
        fun create(): CashFlowService {
            return CashFlowServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
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