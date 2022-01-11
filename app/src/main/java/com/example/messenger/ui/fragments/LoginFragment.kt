package com.example.messenger.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.messenger.R
import com.example.messenger.databinding.LoginFragmentBinding
import com.example.messenger.services.LoadingState
import com.example.messenger.services.constants.Constants.ID_PREFS
import com.example.messenger.services.constants.Constants.USERNAME
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
//        authorization()

        binding.login.setOnClickListener {
            val userName = binding.username.text.toString()
            loginViewModel.login(userName = userName)
            saveUser(userName)
        }
    }

    private fun subscribeLoadingState() {
        loginViewModel.loadingState.observe(viewLifecycleOwner, { loadingState ->
            when(loadingState){
                LoadingState.STARTUDP ->
                { Toast.makeText(context, "startUdp", Toast.LENGTH_SHORT).show()}
                LoadingState.SUCCESS -> {
                    openUsersList()
                }
            }
        })
    }

    private fun authorization() {
        val userName = getUserName()
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

    private fun saveUser(userName: String) {
        val savedPref: SharedPreferences =
            context?.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(USERNAME, userName)
            apply()
        }
    }

    private fun getUserName(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(USERNAME, "")!!
    }
}