package com.example.docsapp2.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.docsapp2.R
import com.example.docsapp2.databinding.FragmentMainBinding
import com.example.docsapp2.presentation.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        binding.btnFromCamera.setOnClickListener {
            openCamera()
        }
        binding.btnFromGallery.setOnClickListener {
            getContent.launch("image/*")
        }
        binding.btnEndPackage.setOnClickListener {
            viewModel.onEndPackage()
        }
    }

    private fun observe() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.fold({
                if (it) {
                    findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                }
                else {
                    showMessage("Something went wrong")
                }
            }, {
                showMessage(it.message.toString())
            })
        }
    }

    private var currentPhotoPath: String? = null
    private var imageUri: Uri? = null
    private fun openCamera() {
        val storageDict = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("camera", ".jpg", storageDict)
        currentPhotoPath = imageFile.absolutePath

        imageUri = FileProvider.getUriForFile(requireContext(), "com.example.docsapp2.fileprovider", imageFile)

        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        takePictureLauncher.launch(takePictureIntent)
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = Bundle().apply {
                    putParcelable("camera", imageUri)
                }
                findNavController().navigate(R.id.action_mainFragment_to_resultFragment, bundle)
            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { localUri ->
//            binding.imageView.setImageURI(localUri)
            localUri?.let {
                val bundle = Bundle().apply {
                    putParcelable("gallery", localUri)
                }
                findNavController().navigate(R.id.action_mainFragment_to_resultFragment, bundle)
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




