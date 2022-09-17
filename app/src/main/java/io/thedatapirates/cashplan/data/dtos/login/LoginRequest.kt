package io.thedatapirates.cashplan.data.dtos.login

import kotlinx.serialization.Serializable

/**
 * Class to represent a login request
 */
@Serializable
data class LoginRequest(
    var username: String,
    var password: String
)