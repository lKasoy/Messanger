package com.example.messenger.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.messenger.R
import com.example.messenger.databinding.LoginFragmentBinding
import com.example.messenger.di.DI
import com.example.messenger.services.LoadingState
import com.example.messenger.services.constants.Constants.ID
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
        binding = LoginFragmentBinding.bind(
            inflater.inflate(R.layout.login_fragment, container, false)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.startConnection()
        subscribeUdpConnection()
        subscribeTcpConnection()

        binding.login.setOnClickListener {
            loginViewModel.startTCPIP()
        }
    }

    private fun subscribeTcpConnection() {
        DI.id.observe(viewLifecycleOwner, { id ->
            if (id != "") {
                var userName = getUserName()
                if (userName == "") {
                    userName = binding.username.text.toString()
                }
                loginViewModel.login(id, userName)
                saveId(id)
                saveUser(userName)
                openUsersList()
                loginViewModel.sendPing(id)
            }

        })
    }


    private fun subscribeUdpConnection() {
        loginViewModel.udpConnection.observe(viewLifecycleOwner, { loadingState ->
            when (loadingState) {
                LoadingState.SUCCESS -> {
                    binding.login.isEnabled = true
                    authorization()
                }
            }
        })
    }

    private fun authorization() {
        val userName = getUserName()
        val id = getID()
        if (userName != "") {
            loginViewModel.startTCPIP()
        }
    }

    private fun openUsersList() {
        parentFragmentManager.commit {
            replace<UsersFragment>(R.id.container)
            setReorderingAllowed(true)
        }
    }

    private fun saveId(id: String) {
        val savedPref: SharedPreferences =
            context?.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(ID, id)
            apply()
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

    private fun getID(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(ID, "")!!
    }

    private fun getUserName(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(USERNAME, "")!!
    }
}