package com.example.btl

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

data class Promotion(val text: String, val color: Int)

class PromotionAdapter(private val promotions: List<Promotion>) :
    RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    inner class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val promotionText: TextView = itemView.findViewById(R.id.promotion_text)
        val cardView: CardView = itemView as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.promotionText.text = promotion.text
        holder.cardView.setCardBackgroundColor(promotion.color)
    }

    override fun getItemCount(): Int {
        return promotions.size
    }
}