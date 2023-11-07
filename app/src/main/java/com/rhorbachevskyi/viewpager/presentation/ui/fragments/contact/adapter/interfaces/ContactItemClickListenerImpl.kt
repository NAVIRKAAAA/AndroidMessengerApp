package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.interfaces

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.ContactsViewModel
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections

class ContactItemClickListenerImpl(
    private val viewModel: ContactsViewModel,
    private val navController: NavController,
    private val deleteUserWithRestore: (Contact) -> Unit
) : ContactItemClickListener {
    override fun onContactClick(
        contact: Contact,
        transitionPairs: Array<Pair<View, String>>
    ) {
        if (viewModel.isMultiselect.value) {
            if (!viewModel.selectContacts.value.contains(contact)) {
                viewModel.addSelectContact(contact)
            } else {
                viewModel.deleteSelectContact(contact)
            }
            if (viewModel.selectContacts.value.isEmpty()) {
                viewModel.changeMultiselectMode()
            }
        } else {
            if (!viewModel.contactList.value.contains(contact)) return
            val extras = FragmentNavigatorExtras(*transitionPairs)
            val direction =
                ViewPagerFragmentDirections.actionViewPagerFragmentToContactProfile(
                    false,
                    contact
                )
            navController.navigate(direction, extras)
        }
    }

    override fun onLongClick(contact: Contact) {
        viewModel.changeMultiselectMode()
        if (viewModel.isMultiselect.value) {
            viewModel.addSelectContact(contact)
        }
    }

    override fun onDeleteClick(contact: Contact) {
        deleteUserWithRestore(contact)
    }
}