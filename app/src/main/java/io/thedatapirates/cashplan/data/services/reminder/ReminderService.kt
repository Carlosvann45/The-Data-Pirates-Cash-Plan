package io.thedatapirates.cashplan.data.services.reminder

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderRequest
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderResponse

interface ReminderService {
    suspend fun getAllReminders(accessToken: String?) : MutableList<ReminderResponse>
    suspend fun createReminder(accessToken: String?, reminder: ReminderRequest): ReminderResponse
    suspend fun deleteReminder(accessToken: String?, reminderId: Long)

    /**
     * Dependency injection for customer service
     */
    companion object {
        fun create(): ReminderService {
            return ReminderServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        accept(ContentType.Application.Json)
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