package com.rhorbachevskyi.viewpager.ui.fragments.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentAddUserBinding
import com.rhorbachevskyi.viewpager.ui.fragments.contact.ContactsViewModel
import com.rhorbachevskyi.viewpager.utils.ext.loadImage


class DialogFragment : AppCompatDialogFragment() {
    private lateinit var binding: FragmentAddUserBinding
    private var photoUri: Uri? = null

    private var viewModel = ContactsViewModel()
    private val requestImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                photoUri = it
                binding.imageViewSignUpExtendedPhoto.loadImage(it.toString())
                binding.imageViewSignUpExtendedMockup.visibility = View.GONE
            }
        }

    fun setViewModel(userViewModel: ContactsViewModel) {
        this.viewModel = userViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_user, null)
        builder.setView(dialogView)
        binding = FragmentAddUserBinding.bind(dialogView)
        setListeners()
        return builder.create()
    }

    private fun setListeners() {
        save()
        navigationBack()
        setPhoto()
    }

    private fun setPhoto() {
        binding.imageViewAddPhotoSignUpExtended.setOnClickListener {
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            requestImageLauncher.launch(request)
        }

    }

    private fun save() {
        binding.buttonSave.setOnClickListener {
            viewModel.addContact(
                Contact(
         binding.textInputEditTextUserName.text.toString(),
                  binding.textInputEditTextCareer.text.toString(),
                photoUri.toString())
            )
            dismiss()
        }
    }

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            dismiss()
        }
    }
}