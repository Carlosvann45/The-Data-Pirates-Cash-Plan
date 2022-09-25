package io.thedatapirates.cashplan.data.dtos.frequency

import kotlinx.serialization.Serializable

/**
 * A class to represent a frequency from the api
 */
@Serializable
data class Frequency(
    val id: Long,
    val name: String
)