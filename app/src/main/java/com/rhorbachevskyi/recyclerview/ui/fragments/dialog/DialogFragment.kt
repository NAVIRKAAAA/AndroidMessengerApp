package com.rhorbachevskyi.recyclerview.ui.fragments.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.recyclerview.R
import com.example.recyclerview.databinding.FragmentAddUserBinding
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.ContactsViewModel
import com.google.android.material.textfield.TextInputEditText


class DialogFragment : AppCompatDialogFragment() {
    private lateinit var binding: FragmentAddUserBinding

    private var userViewModel = ContactsViewModel()
    fun setViewModel(userViewModel: ContactsViewModel) {
        this.userViewModel = userViewModel
    }
    private var adapter = RecyclerViewAdapter()
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
            userViewModel.addUser(
                Contact(
                    dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextFullName).text.toString(),
                    dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextCareer).text.toString(),
                    ""
                ), userViewModel.getUserList().size
            )
            adapter.updateUsers(userViewModel.getUserList())
            adapter.notifyItemInserted(userViewModel.getUserList().size - 1)
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }
}