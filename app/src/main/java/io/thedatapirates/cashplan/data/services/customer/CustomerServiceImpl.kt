package io.thedatapirates.cashplan.data.services.customer

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.createAccount.CreateAccountRequest
import io.thedatapirates.cashplan.data.dtos.createAccount.CreateAccountResponse
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse

/**
 * Implementation of customer service class for api calls
 */
class CustomerServiceImpl(
    private val client: HttpClient
) : CustomerService {

    /**
     * Gets customer information with email and token
     */
    override suspend fun getCustomerInformation(
        email: String?,
        accessToken: String?
    ): CustomerResponse? {
        return client.get {
            url("${HttpRoutes.CUSTOMERS}/$email")
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

    override suspend fun createCustomer(customerInformation: CreateAccountRequest): CreateAccountResponse {
        return client.post {
            url(HttpRoutes.CREATE_CUSTOMER)
            contentType(ContentType.Application.Json)
            body = customerInformation
        }
    }
}