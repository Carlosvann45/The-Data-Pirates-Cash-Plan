package io.thedatapirates.cashplan.data.services.expense

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse
import kotlin.text.get

/**
 * Implements the expense service class
 */
class ExpenseServiceImpl(
    private val client: HttpClient
) : ExpenseService  {

    /**
     * Gets all expenses for a give customer with access token
     */
    override suspend fun getExpensesForCustomer(accessToken: String?): MutableList<ExpenseResponse> {
        return client.get {
            url(HttpRoutes.ALL_EXPENSES)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

}