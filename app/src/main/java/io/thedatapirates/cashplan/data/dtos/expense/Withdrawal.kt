package io.thedatapirates.cashplan.data.dtos.expense

import kotlinx.serialization.Serializable

/**
 * A class to represent an withdrawal from the api
 */
@Serializable
class Withdrawal(
    var id: Long,
    var dateCreated: String,
    var dateUpdated: String,
    var amount: Double
)