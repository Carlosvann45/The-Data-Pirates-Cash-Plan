package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent a stock ticker from the stock api
 */
@Serializable
data class StockTicker(
    var Name: String,
    var Symbol: String
)