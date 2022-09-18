package io.thedatapirates.cashplan.data.services.frequency

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.category.Category
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import io.thedatapirates.cashplan.data.services.category.CategoryService
import io.thedatapirates.cashplan.data.services.category.CategoryServiceImpl
import kotlinx.serialization.json.Json

interface FrequencyService {

    suspend fun getFrequencies(): MutableList<Frequency>

    /**
     * Dependency injection for frequency service
     */
    companion object {
        fun create(): FrequencyService {
            return FrequencyServiceImpl(
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