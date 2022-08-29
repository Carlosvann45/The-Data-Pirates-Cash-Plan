package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent a customer request to the api
 */
@Serializable
class InvestmentRequest(
    var name: String,
    var sector: String,
    var amount: Double,
    var buyPrice: Double
) {
}