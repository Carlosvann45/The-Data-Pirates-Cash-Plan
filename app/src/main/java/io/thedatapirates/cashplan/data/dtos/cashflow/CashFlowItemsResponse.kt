package io.thedatapirates.cashplan.data.dtos.cashflow

import kotlinx.serialization.Serializable

@Serializable
data class CashFlowItemsResponse (
    var name: String,
    var id: Int,
    var deposits: MutableList<Deposit>,
    var frequency: Frequency
    )

