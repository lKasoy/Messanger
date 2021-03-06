package com.example.messenger.services.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.MessageInBinding
import com.example.messenger.databinding.MessageOutBinding
import com.example.messenger.repository.db.entitydb.Message

private const val IN = 0
private const val OUT = 1

class ChatAdapter(
    private val receiverId: String
) : ListAdapter<Message, ChatAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == IN) {
            InMessageViewHolder(
                MessageInBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OutMessageViewHolder(
                MessageOutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Message = currentList[position]
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].receiverId == receiverId ) {
            OUT
        } else {
            IN
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(message: Message)
    }

    class InMessageViewHolder(private val binding: MessageInBinding) : ViewHolder(binding.root) {

        override fun bind(message: Message) {
            binding.apply {
                txtInMessage.text = message.message
            }
        }
    }

    class OutMessageViewHolder(private val binding: MessageOutBinding) : ViewHolder(binding.root) {

        override fun bind(message: Message) {
            binding.apply{
                txtOutMessage.text = message.message
            }
        }
    }
}

