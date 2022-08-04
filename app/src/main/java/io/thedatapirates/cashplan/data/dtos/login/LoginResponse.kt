package io.thedatapirates.cashplan.data.dtos.login

import kotlinx.serialization.Serializable

/**
 * Represents a login response
 */
@Serializable
class LoginResponse(
    var accessToken: String,
    var refresherToken: String
) {
}