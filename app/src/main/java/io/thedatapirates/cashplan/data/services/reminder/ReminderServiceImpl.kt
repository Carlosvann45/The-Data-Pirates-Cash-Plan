package io.thedatapirates.cashplan.data.services.reminder

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderRequest
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderResponse

class ReminderServiceImpl(
    private val client: HttpClient
) : ReminderService {

    override suspend fun createReminder(
        accessToken: String?,
        reminder: ReminderRequest
    ): ReminderResponse {
        return client.post {
            url(HttpRoutes.REMINDERS)
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
            body = reminder
        }
    }

    override suspend fun deleteReminder(accessToken: String?, reminderId: Long) {
        client.delete<String> {
            url(HttpRoutes.REMINDERS.plus("/").plus(reminderId))
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }

}