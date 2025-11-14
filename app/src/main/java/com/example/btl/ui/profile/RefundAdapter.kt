package com.example.btl.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemRefundBinding

class RefundAdapter(private val refunds: List<Refund>) : RecyclerView.Adapter<RefundAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRefundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(refunds[position])
    }

    override fun getItemCount() = refunds.size

    inner class ViewHolder(private val binding: ItemRefundBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(refund: Refund) {
            binding.refundDescription.text = refund.description
            binding.refundDate.text = refund.date
        }
    }
}
