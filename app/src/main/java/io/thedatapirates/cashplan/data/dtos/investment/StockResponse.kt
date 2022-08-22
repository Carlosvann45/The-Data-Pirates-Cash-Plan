package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent each item in the response from the stock api
 */
@Serializable
class StockResponse(
    var c: Double?,
    var h: Double?,
    var l: Double?,
    var o: Double?,
    var pc: Double?
) {
}