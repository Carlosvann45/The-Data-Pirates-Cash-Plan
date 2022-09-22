package io.thedatapirates.cashplan.data.dtos.cashflow

import kotlinx.serialization.Serializable

@Serializable
data class CreateCashFlowItem(
    var name: String,
    var startDate: String,
    var frequencyId: Int,
)


// Make POST REQ to deposit