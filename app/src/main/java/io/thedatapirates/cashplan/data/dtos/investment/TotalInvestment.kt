package io.thedatapirates.cashplan.data.dtos.investment

class TotalInvestment(
    var name: String = "",
    var amount: Double = 0.00,
    var shares: Double = 0.00,
    var currentPrice: Double = 0.00,
    var buyPrice: Double = 0.00,
    var currentP_L: Double = 0.00,
    var currentP_L_Percent: Double = 0.00
) {
}