package io.thedatapirates.cashplan.data.services.login

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.login.LoginRequest
import io.thedatapirates.cashplan.data.dtos.login.LoginResponse

/**
 * Interface for login services
 */
interface LoginService {

    suspend fun loginCustomer(loginRequest: LoginRequest): LoginResponse?

    companion object {
        fun create(): LoginService {
            return LoginServiceImpl(
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