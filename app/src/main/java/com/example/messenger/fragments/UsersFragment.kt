package com.example.messenger.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.R
import com.example.messenger.adapter.UsersAdapter
import com.example.messenger.constants.Constants
import com.example.messenger.constants.Constants.ID
import com.example.messenger.constants.Constants.ID_PREFS
import com.example.messenger.constants.Constants.USERNAME
import com.example.messenger.databinding.FragmentUserListBinding
import com.example.messenger.di.DI
import com.example.messenger.model.User
import com.example.messenger.services.LoadingState
import com.example.messenger.viewmodels.UsersViewModel
import com.example.messenger.viewmodels.factories.UsersListViewModelFactory

class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding

    private var usersAdapter = UsersAdapter(
        onCLick = { user: User ->
            val userBundle = bundleOf(
                ID to user.id,
                USERNAME to user.name
            )
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ChatFragment>(R.id.container, args = userBundle)
                addToBackStack(null)
            }
        }
    )
    private lateinit var usersViewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.bind(
            inflater.inflate(
                R.layout.fragment_user_list,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.adapter = usersAdapter

        val factory = UsersListViewModelFactory(DI.decoratorRepository)
        usersViewModel = ViewModelProvider(this, factory)[UsersViewModel::class.java]

        val list: List<User> = listOf(
            User("dasda", "Sergey"),
            User("dadad", "Svyatoslav")
        )
        usersAdapter.submitList(list)

        subscribeUsersList()
        subscribeLoadingState()
        val id = getSharePrefsId()
        usersViewModel.fetchUsers(id)

        binding.btnLogOut.setOnClickListener {
            usersViewModel.logOut(id, 1)
            setLogOutState()
            goToLoginFragment()

        }
    }

    private fun goToLoginFragment() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<LoginFragment>(R.id.container)
            addToBackStack(null)
        }
    }

    private fun setLogOutState() {
        val savedPref: SharedPreferences =
            context?.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(Constants.STATE, "")
            apply()
        }
    }

    private fun subscribeLoadingState() {
        usersViewModel.loadingState.observe(viewLifecycleOwner, { state ->
            when (state) {
                LoadingState.LOADING -> {
                    Toast.makeText(context, "Loading users...", Toast.LENGTH_SHORT).show()
                }
                LoadingState.SUCCESS -> {
                    Toast.makeText(context, "Users loaded.", Toast.LENGTH_SHORT).show()
                }
                LoadingState.ERROR -> {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getSharePrefsId(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(ID, "")!!
    }

    private fun subscribeUsersList() {
        usersViewModel.users.observe(viewLifecycleOwner, {
            it.let {
                usersAdapter.submitList(usersAdapter.currentList + it)
            }
        })
    }


}