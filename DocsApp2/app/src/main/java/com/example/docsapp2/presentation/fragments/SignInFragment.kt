package com.example.docsapp2.presentation.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.docsapp2.R
import com.example.docsapp2.databinding.FragmentSignInBinding
import com.example.docsapp2.presentation.activity.MainActivity
import com.example.docsapp2.presentation.viewmodel.SignInViewModel
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private val binding by viewBinding(FragmentSignInBinding::bind)
    private val viewModel: SignInViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        binding.btnScanQr.setOnClickListener {
            checkPermissionCamera()
        }
    }

    private fun observe() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.fold({
                if (it) {
                    switchVisibility()
                    findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
                }
                else {
                    showMessage("try another QR-code")
                }
            }, {
                showMessage(it.message.toString())
            })
        }
    }

    private fun checkPermissionCamera() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            showMessage("camera permission required")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            run {
                if (isGranted) {
                    showCamera()
                } else {
                    showMessage("camera permission required")
                }
            }
        }

    private fun showCamera() {
        ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan QR code")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(false)
            scanLauncher.launch(this)
        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        run {
            if (result.contents == null) {
                showMessage("Cancelled")
            } else {
                //showMessage(result.contents)
                viewModel.onVerifyToken(result.contents)
                switchVisibility()
            }
        }
    }

    private fun switchVisibility() {
        with (binding) {
            if (btnScanQr.isVisible) {
                btnScanQr.isVisible = false
                progress.isVisible = true
            }
            else {
                btnScanQr.isVisible = true
                progress.isVisible = false
            }
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