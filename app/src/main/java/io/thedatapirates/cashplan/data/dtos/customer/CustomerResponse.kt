package io.thedatapirates.cashplan.data.dtos.customer

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class CustomerResponse(
    var id: Int,
    var dateCreated: LocalDateTime,
    var dateUpdated: LocalDateTime,
    var firstName: String,
    var lastName: String,
    var username: String
) {
}