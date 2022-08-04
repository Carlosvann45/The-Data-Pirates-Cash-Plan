package io.thedatapirates.cashplan.data.dtos.customer

import kotlinx.serialization.Serializable

/**
 * A class to represent a customer response from the api
 */
@Serializable
class CustomerResponse(
    var id: Int,
    var dateCreated: String,
    var dateUpdated: String,
    var firstName: String,
    var lastName: String,
    var username: String
) {
}