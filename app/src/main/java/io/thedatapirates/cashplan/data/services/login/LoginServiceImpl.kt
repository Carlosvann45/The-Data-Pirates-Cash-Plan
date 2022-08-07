package io.thedatapirates.cashplan.data.services.login

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.login.LoginRequest
import io.thedatapirates.cashplan.data.dtos.login.LoginResponse

/**
 * Implement's logic from login service
 */
class LoginServiceImpl(
    private val client: HttpClient
    ) : LoginService {

    /**
     * logs in customer with given login request
     */
    override suspend fun loginCustomer(loginRequest: LoginRequest): LoginResponse {
        return client.post {
                url(HttpRoutes.LOGIN)
                body = FormDataContent(Parameters.build {
                    append("username", loginRequest.username)
                    append("password", loginRequest.password)
                })
            }
    }
}