package io.thedatapirates.cashplan.data.dtos.reminder

import kotlinx.serialization.Serializable

/**
 * A class to represent a request for a reminder
 */
@Serializable
data class ReminderRequest(
    var name: String,
    var description: String,
    var reminderTime: String,
    var frequencyId: Long,
    var expenseId: Long
)