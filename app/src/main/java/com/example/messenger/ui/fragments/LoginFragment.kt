package com.example.messenger.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.messenger.R
import com.example.messenger.databinding.LoginFragmentBinding
import com.example.messenger.ui.viewmodels.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val loginViewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeLoadingState()
        binding.login.setOnClickListener {
            val userName = binding.username.text.toString()
            loginViewModel.login(userName = userName)
        }
    }

    private fun subscribeLoadingState() {
        loginViewModel.loadingState.observe(viewLifecycleOwner, {
            when (it) {
                true -> {
                    openUsersList()
                }
            }
        })
    }

    private fun openUsersList() {
        parentFragmentManager.commit {
            replace<UsersFragment>(R.id.container)
            setReorderingAllowed(true)
        }
    }
}