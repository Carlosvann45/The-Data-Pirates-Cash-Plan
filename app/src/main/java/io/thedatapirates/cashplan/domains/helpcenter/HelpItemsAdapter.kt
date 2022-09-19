package io.thedatapirates.cashplan.domains.helpcenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.helpCenter.HelpItem

class HelpItemsAdapter(
    private val helpItems: MutableList<HelpItem>
) : RecyclerView.Adapter<HelpItemsAdapter.HelpItemsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HelpItemsAdapter.HelpItemsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.help_center_item, parent, false)

        return HelpItemsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HelpItemsAdapter.HelpItemsViewHolder, position: Int) {
        // add information to layout
        val currentItem = helpItems[position]

        val itemQuestion: TextView = holder.itemView.findViewById(R.id.tvHelpItemQuestion)
        val itemAnswer: TextView = holder.itemView.findViewById(R.id.tvHelpItemAnswer)
        val itemButton: ImageView = holder.itemView.findViewById(R.id.ivOpenHelpItem)

        itemAnswer.text = currentItem.answer
        itemQuestion.text = currentItem.question

        itemButton.setOnClickListener {
            itemAnswer.visibility = when (itemAnswer.visibility) {
                View.GONE -> {
                    itemButton.setImageResource(R.drawable.up_arrow)

                    View.VISIBLE
                }
                else -> {
                    itemButton.setImageResource(R.drawable.down_arrow)

                    View.GONE
                }
            }
        }
    }

    /**
     * Decides on what layout to return based on position
     */
    override fun getItemViewType(position: Int): Int {
        return R.layout.help_center_item
    }

    override fun getItemCount(): Int {
        return helpItems.size
    }

    /**
     * Gets the different items from the view to modify each field on a given layout
     */
    class HelpItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}