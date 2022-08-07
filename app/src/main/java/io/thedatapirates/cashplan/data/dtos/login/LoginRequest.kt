package io.thedatapirates.cashplan.data.dtos.login

import kotlinx.serialization.Serializable

/**
 * Class to represent a login request
 */
@Serializable
class LoginRequest(
    var username: String,
    var password: String
) {
}