package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

@Serializable
data class StockTicker(
    var Name: String,
    var Symbol: String
)