package com.example.messenger.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.messenger.R
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.services.adapter.ChatAdapter
import com.example.messenger.services.constants.Constants
import com.example.messenger.services.constants.Constants.ID
import com.example.messenger.services.constants.Constants.USERNAME
import com.example.messenger.ui.viewmodels.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel by viewModel<ChatViewModel>()
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

        binding.messageList.adapter = chatAdapter

        chatViewModel.refreshMessagesList()

        val userId = requireArguments().getString(ID)
        val userName = requireArguments().getString(USERNAME)
        binding.username.text = userName

        subscribeMessageList()

        binding.sendMessage.setOnClickListener {
            val message = binding.txtMessage.text.toString()
            chatViewModel.sendMessage(getID(), userId!!, message)
            binding.txtMessage.text = null
            chatViewModel.refreshMessagesList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun subscribeMessageList() {
        chatViewModel.messageList.observe(viewLifecycleOwner, {
            chatAdapter.submitList(it)
            binding.messageList.smoothScrollToPosition(binding.messageList.bottom)
            chatAdapter.notifyDataSetChanged()

        })
    }

    private fun getID(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(
                Constants.ID_PREFS,
                AppCompatActivity.MODE_PRIVATE
            )
        return sharedPrefs.getString(ID, "")!!
    }
}