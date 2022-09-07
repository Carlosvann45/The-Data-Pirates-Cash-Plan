package io.thedatapirates.cashplan.data.dtos.expense

import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import io.thedatapirates.cashplan.data.dtos.priortiyLevel.PriorityLevel
import kotlinx.serialization.Serializable

/**
 * A class to represent an expense from the api
 */
@Serializable
class ExpenseResponse(
    var id: Long,
    var dateCreated: String,
    var dateUpdated: String,
    var startDate: String,
    var endDate: String,
    var name: String,
    var category: Category,
    var frequency: Frequency,
    var priorityLevel: PriorityLevel,
    var withdrawals: MutableList<Withdrawal>
)