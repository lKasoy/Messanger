package com.example.messenger.services.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.FragmentUserBinding
import com.example.messenger.repository.servermodel.User

class UsersAdapter(
    private val onCLick: (User) -> Unit,
) : ListAdapter<User, UsersAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result: User = currentList[position]
        holder.bind(result, onCLick)
    }

    class ViewHolder(private val binding: FragmentUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: User, onItemClick: (User) -> Unit) {
            binding.apply {
                txtUserName.text = result.name
                root.setOnClickListener {
                    onItemClick(result)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}