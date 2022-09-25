package io.thedatapirates.cashplan.data.services.expense

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseRequest
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import io.thedatapirates.cashplan.data.dtos.expense.Withdrawal
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * Service class for all expense related request
 */
interface ExpenseService {

    suspend fun getExpensesForCustomer(accessToken: String?): MutableList<ExpenseResponse>

    suspend fun createExpense(accessToken: String?, expense: ExpenseRequest)

    suspend fun editExpense(accessToken: String?, expense: ExpenseRequest)

    suspend fun addWithdrawalForExpense(
        accessToken: String?,
        withdrawal: Withdrawal,
        expenseId: Long
    ): ExpenseResponse

    /**
     * Dependency injection for expense service
     */
    @ExperimentalSerializationApi
    companion object {
        fun create(): ExpenseService {
            return ExpenseServiceImpl(
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