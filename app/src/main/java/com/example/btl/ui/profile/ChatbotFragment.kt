package com.example.btl.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.databinding.FragmentChatbotBinding

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                val userMessage = ChatMessage(messageText, true)
                messages.add(userMessage)
                adapter.notifyItemInserted(messages.size - 1)
                binding.messageInput.text.clear()

                // Get bot response
                val botResponse = getBotResponse(messageText)
                messages.add(botResponse)
                adapter.notifyItemInserted(messages.size - 1)
            }
        }

        // Initial message
        messages.add(ChatMessage("Xin chào, tôi có thể giúp gì cho bạn?", false))
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun getBotResponse(message: String): ChatMessage {
        val lowerCaseMessage = message.lowercase()
        return when {
            lowerCaseMessage.contains("hà nội") -> ChatMessage("Ở Hà Nội, chúng tôi có các khách sạn: Khách sạn Như Ý, Khách sạn ABC.", false)
            lowerCaseMessage.contains("hồ chí minh") -> ChatMessage("Ở Hồ Chí Minh, chúng tôi có Khách sạn XYZ.", false)
            else -> ChatMessage("Xin lỗi, tôi không hiểu yêu cầu của bạn.", false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}