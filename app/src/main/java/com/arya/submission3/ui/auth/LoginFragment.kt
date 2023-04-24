package com.arya.submission3.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.arya.submission3.R
import com.arya.submission3.databinding.FragmentLoginBinding
import com.arya.submission3.utils.Result
import com.arya.submission3.utils.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AuthViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ObjectAnimator.ofFloat(binding.logoLogin, View.ROTATION_Y, -20f, 20f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.materialDivider, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.buttonToRegister, View.ALPHA, 1f).setDuration(100)
            )
            startDelay = 100
        }.start()

        binding.buttonLogin.setOnClickListener {
            viewModel.login(binding.edLoginEmail.editText?.text.toString(), binding.edLoginPassword.editText?.text.toString()).observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Success -> {
                        viewModel.saveUserToken(it.data?.loginResult?.token ?: "")
                        viewModel.setLoading(false)
                    }

                    is Result.Loading -> {
                        viewModel.setLoading(true)
                    }

                    is Result.Error -> {
                        viewModel.setLoading(false)
                        viewModel.setError(it.message ?: "An error occurred")
                    }
                }
            }
        }

        binding.buttonToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}