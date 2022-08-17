package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

@Serializable
class StockData(
    var ticker: String?,
    var name: String?,
    var price: Double?
) {
}