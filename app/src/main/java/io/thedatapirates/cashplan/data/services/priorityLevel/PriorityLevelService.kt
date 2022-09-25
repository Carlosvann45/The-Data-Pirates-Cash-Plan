package io.thedatapirates.cashplan.data.services.priorityLevel

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.thedatapirates.cashplan.data.dtos.priortiyLevel.PriorityLevel

interface PriorityLevelService {

    suspend fun getPriorityLevels(): MutableList<PriorityLevel>

    /**
     * Dependency injection for frequency service
     */
    companion object {
        fun create(): PriorityLevelService {
            return PriorityLevelServiceImpl(
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