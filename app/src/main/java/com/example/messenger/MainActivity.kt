package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.messenger.ui.fragments.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment(savedInstanceState)
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            supportFragmentManager.commit {
                add<LoginFragment>(R.id.container, "loginFragment")
//                replace<LoginFragment>(R.id.container)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
    }
}