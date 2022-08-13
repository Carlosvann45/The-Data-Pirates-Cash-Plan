package io.thedatapirates.cashplan.cashflow

class ExpensesList (
    private val expenses: MutableList<Expense>
    ) {

    fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    fun deleteExpense() {
        expenses.removeAt(expenses.size - 1)
    }

    fun getItemCount(): Int {
        return expenses.size
    }
}
