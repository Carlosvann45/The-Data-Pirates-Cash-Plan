package io.thedatapirates.cashplan.data.dtos.investment

import kotlinx.serialization.Serializable

/**
 * A class to represent a customer response from the api
 */
@Serializable
class InvestmentResponse(
    var id: Int = 0,
    var dateCreated: String = "",
    var dateUpdated: String = "",
    var investmentType: String = "",
    var name: String = "",
    var amount: Double = 0.00,
    var buyPrice: Double = 0.00
) {
}