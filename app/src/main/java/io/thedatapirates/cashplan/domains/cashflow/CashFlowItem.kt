package io.thedatapirates.cashplan.domains.cashflow

data class CashFlowItem(
    var type: String = "Deposit",
    var name: String = "",
    var amount: Float,
    var isMonthly: Boolean = false,
    var isChecked: Boolean = false


)