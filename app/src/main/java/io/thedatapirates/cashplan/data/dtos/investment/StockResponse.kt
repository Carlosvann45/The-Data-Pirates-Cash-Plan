package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent each item in the response from the stock api
 */
@Serializable
data class StockResponse(
    var price: Double?,
    var symbol: String?
)