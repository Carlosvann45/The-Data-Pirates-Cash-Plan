package io.thedatapirates.cashplan.data.services.category

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.category.Category

/**
 * Implements category service
 */
class CategoryServiceImpl(
    private val client: HttpClient
) : CategoryService {

    /**
     * Gets categories from api
     */
    override suspend fun getCategories(): MutableList<Category> {
        return client.get {
            url(HttpRoutes.ALL_CATEGORIES)
        }
    }
}