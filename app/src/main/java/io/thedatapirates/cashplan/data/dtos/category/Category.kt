package io.thedatapirates.cashplan.data.dtos.category

import kotlinx.serialization.Serializable

/**
 * A class to represent a category from the api
 */
@Serializable
data class Category(
    val id: Long,
    val name: String
)