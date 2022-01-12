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
import com.example.messenger.services.LoadingState
import com.example.messenger.services.SharedPrefs
import com.example.messenger.ui.viewmodels.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val loginViewModel by viewModel<LoginViewModel>()
    private var sharedPrefs: SharedPrefs? = null

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
        sharedPrefs = SharedPrefs(requireContext())
        authorization()
        binding.login.setOnClickListener {
            val userName = binding.username.text.toString()
            loginViewModel.login(userName = userName)
            sharedPrefs!!.saveUser(userName = userName)
        }
    }

    private fun subscribeLoadingState() {
        loginViewModel.loadingState.observe(viewLifecycleOwner, { loadingState ->
            when (loadingState) {
                LoadingState.SUCCESS -> {
                    openUsersList()
                }
            }
        })
    }

    private fun authorization() {
        val userName = sharedPrefs!!.getUserName()
        if (userName != "") {
            loginViewModel.login(userName = userName)
        }
    }

    private fun openUsersList() {
        parentFragmentManager.commit {
            replace<UsersFragment>(R.id.container)
            setReorderingAllowed(true)
        }
    }
}