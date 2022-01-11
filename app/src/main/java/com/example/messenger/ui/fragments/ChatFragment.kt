package com.example.messenger.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.services.adapter.ChatAdapter
import com.example.messenger.services.constants.Constants.ID
import com.example.messenger.services.constants.Constants.USERNAME
import com.example.messenger.ui.viewmodels.ChatViewModel
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel by viewModel<ChatViewModel>()

    private lateinit var chatAdapter : ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receiverId = requireArguments().getString(ID)
        val receiverUserName = requireArguments().getString(USERNAME)
        val sender = chatViewModel.getCurrentUser()

        val receiver = User(receiverId!!, receiverUserName!!)
        chatAdapter = ChatAdapter(sender, receiver)

        binding.messageList.adapter = chatAdapter

        binding.username.text = receiver.name

        subscribeNewMessage()

        binding.sendMessage.setOnClickListener {
            val message: Message =
                Message(
                    id = UUID.randomUUID().toString(),
                    senderId = sender.id,
                    senderName = sender.name,
                    receiverId = receiver.id,
                    receiverName = receiver.name,
                    message =  binding.txtMessage.text.toString()
                )
            chatViewModel.sendMessage(receiver.id, message.message)

            chatAdapter.submitList(chatAdapter.currentList + message) {
                binding.messageList.smoothScrollToPosition(binding.messageList.bottom)
            }
            binding.txtMessage.text = null
        }
    }

    private fun subscribeNewMessage() {
        chatViewModel.newMessage.observe(viewLifecycleOwner, {
            chatAdapter.submitList(chatAdapter.currentList + it) {
                binding.messageList.smoothScrollToPosition(binding.messageList.bottom)
            }
        })
    }

}