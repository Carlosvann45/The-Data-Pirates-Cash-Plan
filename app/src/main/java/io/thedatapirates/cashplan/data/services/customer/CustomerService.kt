package io.thedatapirates.cashplan.data.services.customer

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.customer.CustomerResponse

/**
 * Interface method for customer api calls
 */
interface CustomerService {

    suspend fun getCustomerInformation(email: String?, accessToken: String?): CustomerResponse?

    companion object {
        fun create(): CustomerService {
            return CustomerServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}