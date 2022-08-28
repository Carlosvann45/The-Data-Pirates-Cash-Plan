package io.thedatapirates.cashplan.data.dtos.createAccount

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    var firstName: String,
    var lastName: String,
    var username: String,
    var password: String
)