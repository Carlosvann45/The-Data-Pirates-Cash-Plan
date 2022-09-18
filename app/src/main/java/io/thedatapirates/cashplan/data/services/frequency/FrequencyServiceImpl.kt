package io.thedatapirates.cashplan.data.services.frequency

import io.ktor.client.*
import io.ktor.client.request.*
import io.thedatapirates.cashplan.constants.HttpRoutes
import io.thedatapirates.cashplan.data.dtos.frequency.Frequency
import kotlin.text.get

/**
 * A class for frequency services
 */
class FrequencyServiceImpl(
    private val client: HttpClient
) : FrequencyService {
    override suspend fun getFrequencies(): MutableList<Frequency> {
        return client.get {
            url(HttpRoutes.ALL_FREQUENCIES)
        }
    }
}