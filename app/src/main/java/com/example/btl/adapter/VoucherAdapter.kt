package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.model.Voucher

class VoucherAdapter(private val voucherList: List<Voucher>) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = voucherList[position]
        holder.voucherImage.setImageResource(voucher.imageResId)
        holder.voucherTitle.text = voucher.title
        holder.voucherDescription.text = voucher.description
    }

    override fun getItemCount(): Int {
        return voucherList.size
    }

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val voucherImage: ImageView = itemView.findViewById(R.id.voucher_image)
        val voucherTitle: TextView = itemView.findViewById(R.id.voucher_title)
        val voucherDescription: TextView = itemView.findViewById(R.id.voucher_description)
    }
}