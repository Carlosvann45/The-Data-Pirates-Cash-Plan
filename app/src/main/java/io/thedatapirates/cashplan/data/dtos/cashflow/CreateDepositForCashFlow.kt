package io.thedatapirates.cashplan.data.dtos.cashflow

import kotlinx.serialization.Serializable

@Serializable
data class Deposit (
    var amount: Float
)

@Serializable
data class Frequency (
    var dateCreated: String,
    var dateUpdated: String,
    var id: Int,
    var name: String
)
