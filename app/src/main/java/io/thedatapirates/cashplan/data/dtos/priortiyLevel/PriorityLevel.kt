package io.thedatapirates.cashplan.data.dtos.priortiyLevel

import kotlinx.serialization.Serializable

/**
 * A class to represent a priority level from the api
 */
@Serializable
data class PriorityLevel(
    val id: Long,
    val level: String,
    val description: String
)