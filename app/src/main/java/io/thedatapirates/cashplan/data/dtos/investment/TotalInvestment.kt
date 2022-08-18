package io.thedatapirates.cashplan.data.dtos.investment

import android.graphics.Color

/**
 * A class to represent a total investment
 */
class TotalInvestment(
    var color: Int = 0,
    var name: String = "",
    var currentAmount: Double = 0.00,
    var totalAmount: Double = 0.00,
    var shares: Double = 0.00,
    var currentPrice: Double = 0.00,
    var buyPrice: Double = 0.00,
    var currentP_L: Double = 0.00,
    var currentP_L_Percent: Double = 0.00
) {
}