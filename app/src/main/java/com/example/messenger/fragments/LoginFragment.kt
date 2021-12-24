package com.example.messenger.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider

import com.example.messenger.R
import com.example.messenger.constants.Constants.ID
import com.example.messenger.constants.Constants.ID_PREFS
import com.example.messenger.constants.Constants.STATE
import com.example.messenger.databinding.LoginFragmentBinding
import com.example.messenger.di.DI
import com.example.messenger.services.LoadingState
import com.example.messenger.viewmodels.LoginViewModel
import com.example.messenger.viewmodels.factories.LoginViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var loginViewModel: LoginViewModel

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

        val factory = LoginViewModelFactory(DI.decoratorRepository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.startConnection()
        subscribeUdpConnection()
        subscribeTCPIPConnection()

        binding.login.setOnClickListener {
            val userName = binding.username.text.toString()
            loginViewModel.login(userName)
            saveUser(userName)
        }
    }

    private fun subscribeUdpConnection() {
        loginViewModel.loadingUdp.observe(viewLifecycleOwner, { state ->
            when (state) {
                LoadingState.SUCCESS -> {
                    authorization()
                }
                LoadingState.ERROR -> {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun authorization() {
        val userName = getState()
        if (userName != "") {
            loginViewModel.login(userName)
        }
    }

    private fun subscribeTCPIPConnection() {
        loginViewModel.loadingTCPIP.observe(viewLifecycleOwner, { state ->
            when (state) {
                LoadingState.SUCCESS -> {
                    val id = loginViewModel.getId()
                    saveId(id)
                    loginViewModel.sendPing(id)
                    openUsersList()
                }
                LoadingState.ERROR -> {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun openUsersList() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
                .replace<UsersFragment>(R.id.container)
                .addToBackStack(null)
        }
    }

    private fun saveUser(userName: String) {
        val savedPref: SharedPreferences =
            context?.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(STATE, userName)
            apply()
        }
    }

    private fun getState(): String {
        val sharedPrefs =
            requireContext().getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(STATE, "")!!
    }

    private fun saveId(id: String) {
        val savedPref: SharedPreferences =
            context?.getSharedPreferences(ID_PREFS, AppCompatActivity.MODE_PRIVATE) ?: return
        with(savedPref.edit()) {
            putString(ID, id)
            apply()
        }
    }
}