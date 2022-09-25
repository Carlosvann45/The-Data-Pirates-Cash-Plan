package io.thedatapirates.cashplan.constants

/**
 * A class for constant string variables
 */
object HttpRoutes {
    private const val BASE_URL = "https://the-data-pirates-cash-plan.herokuapp.com"
    private const val STOCK_URL = "https://the-data-pirates-stock-api.herokuapp.com"
    const val LOGIN = "$BASE_URL/customers/login"
    const val CUSTOMERS = "$BASE_URL/customers"
    const val FORGOT_PASSWORD = "$BASE_URL/verifications/password/forgot"
    const val INVESTMENT = "$BASE_URL/investments"
    const val STOCK_PRICES = "$STOCK_URL/stock/data/quote?symbols="
    const val CREATE_CUSTOMER = "$CUSTOMERS/create"
    const val STOCK_TICKERS = "$STOCK_URL/stock/tickers/all"
    const val EXPENSES = "$BASE_URL/expenses"
    const val EXPENSE_WITHDRAWAL = "$EXPENSES/withdrawal/for/"
    const val REMINDERS = "$BASE_URL/reminders"
    const val ALL_CATEGORIES = "$BASE_URL/categories"
    const val ALL_FREQUENCIES = "$BASE_URL/frequencies"
    const val ALL_PRIORITY_LEVELS = "$BASE_URL/priorityLevels"
    const val CASH_FLOWS = "$BASE_URL/cashFlows"
}