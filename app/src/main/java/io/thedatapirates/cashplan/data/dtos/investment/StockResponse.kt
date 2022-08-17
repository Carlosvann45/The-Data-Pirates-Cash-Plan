package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

@Serializable
class StockResponse(
    val data: MutableList<StockData> = mutableListOf()
    ) {
}