package io.thedatapirates.cashplan.data.dtos.cashflow

import kotlinx.serialization.Serializable

@Serializable
data class DepositResponse (
    var amount: Float,
    var id: Int,
    val dateCreated: String,
    var dateUpdated: String
)
