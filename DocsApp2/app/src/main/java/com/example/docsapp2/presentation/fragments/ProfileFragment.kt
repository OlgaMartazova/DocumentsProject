package com.example.docsapp2.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.docsapp2.R
import com.example.docsapp2.databinding.FragmentProfileBinding
import com.example.docsapp2.presentation.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        binding.btnStartPackage.setOnClickListener {
            viewModel.onStartPackage()
        }
        binding.btnSignOut.setOnClickListener {
            viewModel.onSignOut()
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }
    }

    private fun observe() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.fold({
                if (it) {
                    findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
                }
                else {
                    showMessage("Something went wrong")
                }
            }, {
                showMessage(it.message.toString())
            })
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}