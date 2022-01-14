package com.example.messenger.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.services.adapter.ChatAdapter
import com.example.messenger.services.constants.Constants.ID
import com.example.messenger.services.constants.Constants.RECEIVER_NAME
import com.example.messenger.ui.viewmodels.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel by viewModel<ChatViewModel>(parameters = {
        parametersOf(receiverId)
    })
    private val receiverId by lazy {
        requireArguments().getString(ID) ?: throw IllegalStateException("No id")
    }
    private val receiverName by lazy {
        requireArguments().getString(RECEIVER_NAME) ?: throw IllegalStateException("No name")
    }
    private val chatAdapter: ChatAdapter by lazy { ChatAdapter(receiverId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.messageList.adapter = chatAdapter
        binding.txtReceiverName.text = receiverName

        subscribeListMessages()
        chatViewModel.getMessageList()

        binding.sendMessage.setOnClickListener {
            chatViewModel.sendMessage(binding.txtMessage.text.toString())
            binding.txtMessage.text = null
        }
    }

    private fun subscribeListMessages() {
        chatViewModel.listMessages.observe(viewLifecycleOwner, {
            chatAdapter.submitList(it) {
                binding.messageList.smoothScrollToPosition(binding.messageList.bottom)
            }
        })
    }
}