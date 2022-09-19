package io.thedatapirates.cashplan.data.services.expense

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseRequest
import io.thedatapirates.cashplan.data.dtos.expense.ExpenseResponse

/**
 * Implements the expense service class
 */
class ExpenseServiceImpl(
    private val client: HttpClient
) : ExpenseService {

    /**
     * Gets all expenses for a give customer with access token
     */
    override suspend fun getExpensesForCustomer(accessToken: String?): MutableList<ExpenseResponse> {
        return client.get {
            url(HttpRoutes.EXPENSES)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

    override suspend fun createExpense(accessToken: String?, expense: ExpenseRequest) {
        return client.post {
            url(HttpRoutes.EXPENSES)
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = expense
        }
    }

    override suspend fun editExpense(accessToken: String?, expense: ExpenseRequest) {
        return client.put {
            url(HttpRoutes.EXPENSES.plus("/").plus(expense.id))
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = expense
        }
    }

}