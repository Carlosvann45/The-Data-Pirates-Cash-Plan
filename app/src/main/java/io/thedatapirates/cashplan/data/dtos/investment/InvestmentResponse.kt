package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent a customer response from the api
 */
@Serializable
class InvestmentResponse(
    var id: Int,
    var dateCreated: String,
    var dateUpdated: String,
    var investmentType: String,
    var name: String,
    var sector: String,
    var amount: Double,
    var buyPrice: Double
) {
}