package com.rhorbachevskyi.viewpager.ui.fragments.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.ui.fragments.contact.ContactsViewModel
import com.google.android.material.textfield.TextInputEditText
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentAddUserBinding


class DialogFragment : AppCompatDialogFragment() {
    private lateinit var binding: FragmentAddUserBinding

    private var userViewModel = ContactsViewModel()
    private var adapter = RecyclerViewAdapter()
    fun setViewModel(userViewModel: ContactsViewModel) {
        this.userViewModel = userViewModel
    }
    fun setAdapter(recyclerViewAdapter: RecyclerViewAdapter) {
        adapter = recyclerViewAdapter
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_user, null)
        builder.setView(dialogView)
        binding = FragmentAddUserBinding.bind(dialogView)

        binding.buttonSave.setOnClickListener {
            userViewModel.addContact(
                Contact(
                    dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextFullName).text.toString(),
                    dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextCareer).text.toString(),
                    ""
                ), userViewModel.getContactsList().size
            )
            adapter.updateContacts(userViewModel.getContactsList())
            adapter.notifyItemInserted(userViewModel.getContactsList().size - 1)
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }
}