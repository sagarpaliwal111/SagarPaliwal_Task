package com.sagarpaliwal_task.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sagarpaliwal_task.R
import com.sagarpaliwal_task.core.model.UserHolding
import com.sagarpaliwal_task.databinding.ItemHoldingBinding
import java.text.DecimalFormat

class HoldingsAdapter : ListAdapter<UserHolding, HoldingsAdapter.HoldingViewHolder>(HoldingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = ItemHoldingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HoldingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HoldingViewHolder(
        private val binding: ItemHoldingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("#,##0.00")

        fun bind(holding: UserHolding) {
            binding.apply {
                // Set stock symbol
                textSymbol.text = holding.symbol

                // Set quantity
                textQuantity.text = "NET QTY: ${holding.quantity}"

                // Set LTP
                textLtp.text = "LTP: ₹${decimalFormat.format(holding.ltp)}"

                // Calculate and set P&L
                val currentValue = holding.ltp * holding.quantity
                val investmentValue = holding.avgPrice * holding.quantity
                val pnl = currentValue - investmentValue

                val pnlText = if (pnl >= 0) {
                    "+₹${decimalFormat.format(pnl)}"
                } else {
                    "₹${decimalFormat.format(pnl)}"
                }

                textPnl.text = "P&L: $pnlText"

                // Set P&L color
                val pnlColor = if (pnl >= 0) {
                    ContextCompat.getColor(root.context, R.color.profit_green)
                } else {
                    ContextCompat.getColor(root.context, R.color.loss_red)
                }
                textPnl.setTextColor(pnlColor)

                // Show T1 Holding tag for specific conditions (you can customize this logic)
                if (holding.symbol == "IDEA" && holding.quantity == 3) {
                    tagT1Holding.visibility = android.view.View.VISIBLE
                } else {
                    tagT1Holding.visibility = android.view.View.GONE
                }
            }
        }
    }

    class HoldingDiffCallback : DiffUtil.ItemCallback<UserHolding>() {
        override fun areItemsTheSame(oldItem: UserHolding, newItem: UserHolding): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: UserHolding, newItem: UserHolding): Boolean {
            return oldItem == newItem
        }
    }
}
