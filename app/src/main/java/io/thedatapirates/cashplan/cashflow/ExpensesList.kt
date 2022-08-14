package io.thedatapirates.cashplan.cashflow

import android.util.Log

class ExpensesList (
    private val expenses: MutableList<Expense>
    ) {

    fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    fun deleteExpense(name: String) {
        expenses.removeAll { expense ->
            expense.name == name
        }
    }

    fun getItemCount(): Int {
        return expenses.size
    }

    fun getExpensesTotal() : Float {
        var total= 0.0f;
        for (expense in expenses) {
            total += expense.amount
        }
        return total
    }
}
