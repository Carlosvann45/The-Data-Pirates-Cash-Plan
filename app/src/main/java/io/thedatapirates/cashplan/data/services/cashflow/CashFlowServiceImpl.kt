package io.thedatapirates.cashplan.data.services.cashflow

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.thedatapirates.cashplan.data.dtos.cashflow.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import io.thedatapirates.cashplan.data.dtos.cashflow.CreateCashFlowItem

/**
 * Implementation of customer service class for api calls
 */
class CashFlowServiceImpl(
    private val client: HttpClient
) : CashFlowService {


    override suspend fun getCashFlow(accessToken: String?): MutableList<CashFlowItemsResponse> {
        return client.get {
            url(HttpRoutes.CASH_FLOWS)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

    override suspend fun createCashFlow(cashFlowInformation: CreateCashFlowItem, accessToken: String?): CashFlowItemsResponse {
        return client.post {
            url(HttpRoutes.CASH_FLOWS)
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = cashFlowInformation
        }
    }

    override suspend fun createDepositForCashFlow(amount: Float, id: Int, accessToken: String?) {
        return client.put {
            url("${HttpRoutes.CASH_FLOWS}/deposit/to/$id")
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = { amount }
        }
    }

//    override suspend fun removeCashFlow(id: Int, accessToken: String?) {
//        client.delete {
//            url("${HttpRoutes.CASH_FLOWS}/$id")
//            headers {
//                append("Authorization", "Bearer $accessToken")
//            }
//        }
//    }
}