package com.example.zadanie.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentSignUpBinding
import com.example.zadanie.helpers.Injection
import com.example.zadanie.helpers.PreferenceData
import com.example.zadanie.ui.viewmodels.AuthViewModel

class SignUpFragment : Fragment() {
    private lateinit var _binding: FragmentSignUpBinding
    val binding: FragmentSignUpBinding get() = _binding

    private lateinit var _authViewModel: AuthViewModel
    val authViewModel: AuthViewModel get() = _authViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _authViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isNotBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_bars)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = authViewModel
        }

        binding.signup.setOnClickListener {
            if (binding.username.text.toString().isNotBlank() && binding.password.text.toString().isNotBlank()
                && binding.password.text.toString().compareTo(binding.repeatPassword.text.toString())==0) {
                authViewModel.signup(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            } else if (binding.username.text.toString().isBlank() || binding.password.text.toString().isBlank()){
                authViewModel.show("Fill in name and password")
            } else {
                authViewModel.show("Passwords must be same")
            }
        }

        binding.login.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            view.findNavController().navigate(action)
        }

        authViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(),it)
                val action = SignUpFragmentDirections.actionSignUpFragmentToBarsFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }

    }
}