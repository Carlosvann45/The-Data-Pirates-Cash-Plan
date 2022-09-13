package io.thedatapirates.cashplan.data.dtos.expense

import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import io.thedatapirates.cashplan.data.dtos.priortiyLevel.PriorityLevel
import kotlinx.serialization.Serializable

/**
 * A class to represent an expense from the api
 */
@Serializable
data class ExpenseResponse(
    var id: Long = 0,
    var dateCreated: String = "",
    var dateUpdated: String = "",
    var startDate: String = "",
    var endDate: String? = null,
    var name: String = "",
    var category: Category = Category(0, ""),
    var frequency: Frequency = Frequency(0, ""),
    var priorityLevel: PriorityLevel = PriorityLevel(0, "", ""),
    var withdrawals: MutableList<Withdrawal> = mutableListOf()
)