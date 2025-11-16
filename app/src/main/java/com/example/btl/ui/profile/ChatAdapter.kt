package com.example.btl.ui.profile

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.databinding.ItemChatMessageBinding

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            binding.messageText.text = chatMessage.text

            val params = binding.messageText.layoutParams as RelativeLayout.LayoutParams
            if (chatMessage.isUserMessage) {
                binding.messageText.setBackgroundResource(R.drawable.bg_user_message)
                binding.messageText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                params.addRule(RelativeLayout.ALIGN_PARENT_END)
            } else {
                binding.messageText.setBackgroundResource(R.drawable.bg_chat_message)
                binding.messageText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                params.removeRule(RelativeLayout.ALIGN_PARENT_END)
            }
            binding.messageText.layoutParams = params
        }
    }
}
