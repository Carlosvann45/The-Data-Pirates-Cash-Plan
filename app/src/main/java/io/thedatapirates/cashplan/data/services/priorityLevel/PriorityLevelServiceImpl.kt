package io.thedatapirates.cashplan.data.services.priorityLevel

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.priortiyLevel.PriorityLevel

class PriorityLevelServiceImpl(
    private val client: HttpClient
) : PriorityLevelService {
    override suspend fun getPriorityLevels(): MutableList<PriorityLevel> {
        return client.get {
            url(HttpRoutes.ALL_PRIORITY_LEVELS)
        }
    }
}