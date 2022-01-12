package com.example.messenger.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.messenger.R
import com.example.messenger.databinding.FragmentUserListBinding
import com.example.messenger.repository.servermodel.User
import com.example.messenger.services.adapter.UsersAdapter
import com.example.messenger.services.constants.Constants.ID
import com.example.messenger.services.constants.Constants.ID_PREFS
import com.example.messenger.services.constants.Constants.USERNAME
import com.example.messenger.ui.viewmodels.UsersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding

    private var usersAdapter = UsersAdapter(
        onCLick = { user: User ->
            val userBundle = bundleOf(
                ID to user.id,
                USERNAME to user.name
            )
            parentFragmentManager.commit {
                replace<ChatFragment>(R.id.container, "chatFragment", args = userBundle)
                setReorderingAllowed(true)
                addToBackStack("usersFragment")
            }
        }
    )
    private val usersViewModel by viewModel<UsersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.adapter = usersAdapter

        subscribeUsersList()
        usersViewModel.sendGetUsers()

        binding.btnLogOut.setOnClickListener {
            usersViewModel.logOut()
            resetUserName(requireContext())
            goToLoginFragment()
        }

        binding.btnRenew.setOnClickListener {
            usersViewModel.sendGetUsers()
        }
    }

    private fun goToLoginFragment() {
        parentFragmentManager.commit {
            parentFragmentManager.findFragmentByTag("chatFragment")?.let { remove(it) }
            parentFragmentManager.findFragmentByTag("usersFragment")?.let { remove(it) }
            replace<LoginFragment>(R.id.container, "loginFragment")
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    private fun resetUserName(context: Context) {
        val savedPref: SharedPreferences =
            context.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(USERNAME, "")
            apply()
        }
    }

    private fun subscribeUsersList() {
        usersViewModel.users.observe(viewLifecycleOwner, {
            usersAdapter.submitList(it)
        })
    }
}