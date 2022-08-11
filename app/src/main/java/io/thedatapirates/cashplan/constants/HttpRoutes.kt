package io.thedatapirates.cashplan.constants

/**
 * A class for constant string variables
 */
object HttpRoutes {
    private const val BASE_URL = "http://192.168.1.58:8085"
    const val LOGIN = "$BASE_URL/customers/login"
    const val CUSTOMERS = "$BASE_URL/customers"
    const val FORGOT_PASSWORD = "$BASE_URL/verifications/password/forgot"
}