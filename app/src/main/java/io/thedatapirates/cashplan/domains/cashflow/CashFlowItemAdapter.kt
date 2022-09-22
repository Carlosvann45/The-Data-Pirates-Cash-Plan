package io.thedatapirates.cashplan.domains.cashflow

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.cashflow.CashFlowItemsResponse
import io.thedatapirates.cashplan.data.dtos.cashflow.DepositResponse
import kotlinx.android.synthetic.main.item_deposit_list.view.*

class CashFlowItemAdapter(
    private val depositList: CashFlowItemsResponse,

) : RecyclerView.Adapter<CashFlowItemAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_deposit_list,
                parent,
                false

            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int){
        val curExpense = depositList.deposits[position]
        holder.itemView.apply {
            tvDepositListItemValue.text = curExpense.amount.toString()
            tvDepositItemDate.text = depositList.startDate
        }

        holder.itemView.setOnClickListener {
            Navigation.findNavController(holder.itemView).navigate(R.id.navCashFlow)
        }
    }

    override fun getItemCount(): Int {
        return depositList.deposits.size
    }

    fun depositsTotal() : Float {
       var total = 0f

        for (deposit in depositList.deposits) {
            Log.i(deposit.toString(), "DEPOSIT")
            total += deposit.amount
        }

        return total
    }
}