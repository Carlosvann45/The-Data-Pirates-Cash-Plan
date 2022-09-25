package io.thedatapirates.cashplan.data.dtos.expense

import kotlinx.serialization.Serializable

/**
 * A class to represent a expense request to the backend
 */
@Serializable
data class ExpenseRequest(
    var id: Long,
    var name: String,
    var startDate: String,
    var endDate: String?,
    var frequencyId: Long,
    var categoryId: Long,
    var priorityLevelId: Long
)