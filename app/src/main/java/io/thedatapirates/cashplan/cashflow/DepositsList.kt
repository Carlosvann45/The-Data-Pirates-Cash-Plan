package io.thedatapirates.cashplan.cashflow

import android.util.Log

class DepositsList (
    private val deposits: MutableList<Deposit>
) {

    fun addDeposit(deposit: Deposit) {
        deposits.add(deposit)
    }

    fun deleteDeposit(name: String) {
        deposits.removeAll { deposit ->
            deposit.name == name
        }
    }

    fun getItemCount(): Int {
        return deposits.size
    }

    fun getDepositsTotal() : Float {
        var total= 0.0f;
        for (deposit in deposits) {
            total += deposit.amount
        }
        return total
    }
}