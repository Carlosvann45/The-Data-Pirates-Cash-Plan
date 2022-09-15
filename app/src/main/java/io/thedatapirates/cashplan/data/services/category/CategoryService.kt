package io.thedatapirates.cashplan.data.services.category

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.category.Category

/**
 * Interface for category methods
 */
interface CategoryService {

    suspend fun getCategories(): MutableList<Category>

    /**
     * Dependency injection for customer service
     */
    companion object {
        fun create(): CategoryService {
            return CategoryServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        })
                    }
                }
            )
        }
    }
}