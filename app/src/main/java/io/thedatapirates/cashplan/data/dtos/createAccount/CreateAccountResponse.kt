package io.thedatapirates.cashplan.data.dtos.createAccount

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountResponse(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String
)