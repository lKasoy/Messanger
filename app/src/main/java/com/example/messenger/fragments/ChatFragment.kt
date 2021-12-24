package com.example.messenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.adapter.ChatAdapter
import com.example.messenger.constants.Constants.ID
import com.example.messenger.constants.Constants.USERNAME
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.databinding.FragmentUserListBinding
import com.example.messenger.di.DI
import com.example.messenger.model.User
import com.example.messenger.viewmodels.ChatViewModel
import com.example.messenger.viewmodels.UsersViewModel
import com.example.messenger.viewmodels.factories.ChatViewModelFactory
import com.example.messenger.viewmodels.factories.UsersListViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private val chatAdapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.bind(
            inflater.inflate(
                R.layout.fragment_chat,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true
        binding.messageList.layoutManager = layoutManager
        binding.messageList.adapter = chatAdapter

        val factory = ChatViewModelFactory(DI.decoratorRepository)
        chatViewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        val userId = requireArguments().getString(ID)
        val userName = requireArguments().getString(USERNAME)
        binding.username.text = userName

        chatViewModel.messageList.observe(viewLifecycleOwner, {
            it.let {
                chatAdapter.submitList(chatAdapter.currentList + it)
            }
        })

        binding.sendMessage.setOnClickListener {
            val message = binding.txtMessage.text.toString()
            chatViewModel.sendMessage(userId!!, userName!!, message)
            binding.txtMessage.text = null
        }
    }
}