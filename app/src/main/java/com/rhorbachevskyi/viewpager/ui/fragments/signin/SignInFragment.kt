package com.rhorbachevskyi.viewpager.ui.fragments.signin

import android.os.Bundle
import android.view.View
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment

class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserver()
    }

    private fun setListeners() {
        login()
        signUp()
    }


    private fun signUp() {
        binding.textViewSignUp.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun login() {
        binding.buttonLogin.setOnClickListener {

        }
    }

    private fun setObserver() {

    }
}