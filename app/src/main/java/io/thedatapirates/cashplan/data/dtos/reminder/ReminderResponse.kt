package io.thedatapirates.cashplan.data.dtos.reminder

import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import kotlinx.serialization.Serializable

/**
 * A data class to represent a reminder from the database
 */
@Serializable
data class ReminderResponse(
    val id: Long = 0,
    val dateCreated: String = "",
    val dateUpdated: String = "",
    var name: String = "",
    var description: String = "",
    var reminderTime: String = "",
    var frequency: Frequency = Frequency(0, "")
)