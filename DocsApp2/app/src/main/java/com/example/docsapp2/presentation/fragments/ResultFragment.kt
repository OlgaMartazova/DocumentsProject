package com.example.docsapp2.presentation.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.docsapp2.R
import com.example.docsapp2.data.response.DocResponse
import com.example.docsapp2.databinding.FragmentResultBinding
import com.example.docsapp2.presentation.utils.FileUtils
import com.example.docsapp2.presentation.viewmodel.ResultViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultFragment : Fragment(R.layout.fragment_result) {
    private val binding by viewBinding(FragmentResultBinding::bind)
    private val viewModel: ResultViewModel by viewModel()

    private val cameraUri: Uri? by lazy {
        arguments?.getParcelable("camera")
    }

    private val galleryUri: Uri? by lazy {
        arguments?.getParcelable("gallery")
    }

    private lateinit var docResponse: DocResponse

    private var currentUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        cameraUri?.let {
            uploadFile(it)
            currentUri = it
//            FileUtils.clearFilesInPicturesDirectory(requireContext())
        }

        galleryUri?.let {
            uploadFile(it)
            currentUri = it
        }
        binding.resultFields.btnSave.setOnClickListener {
            viewModel.onProcessResult(docResponse.id, true)
        }
        binding.resultFields.btnRetry.setOnClickListener {
            viewModel.onProcessResult(docResponse.id, false)
        }
    }

    private fun observe() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.fold({
                docResponse = it
                with(binding) {
                    progress.isVisible = false
                    resultFields.root.isVisible = true
                    setFields(it)
                }
            }, {
                showMessage(it.message.toString())
            })
        }
        viewModel.processResult.observe(viewLifecycleOwner) { result ->
            result.fold({
                if (it) findNavController().navigate(R.id.action_resultFragment_to_mainFragment)
                else showMessage("something went wrong")
            }, {
                showMessage(it.message.toString())
            })
        }
    }

    private fun setFields(doc: DocResponse) {
        with(binding.resultFields) {
            imageView.setImageURI(currentUri)
            tvDocType.text = doc.docType
            cvPassportFields.isVisible = doc.docType != "None"
            doc.result?.let {
                tvPassportType.text = it.passportType
                tvIssuingState.text = it.issuingState
                tvLastName.text = it.lastName
                tvFirstName.text = it.firstName
                tvPassportNumber.text = it.passportNumber
                tvNationality.text = it.nationality
                tvDateOfBirth.text = it.dateOfBirth
                tvSex.text = it.sex
                tvDateOfExpiry.text = it.dateOfExpiry
            }
        }
    }

    private fun uploadFile(uri: Uri) {
        FileUtils.getFileFromUriNew(requireContext(), uri)?.let {
            viewModel.onUploadFile(it)
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