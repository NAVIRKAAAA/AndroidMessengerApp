package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.AddContactViewModel
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.AddContactsFragmentDirections

class UserItemClickListenerImpl(
    private val viewModel: AddContactViewModel,
    private val navController: NavController
) : UserItemClickListener {
    override fun onAddClick(contact: Contact) {
        viewModel.addContact(
            UserDataHolder.userData.user.id,
            contact,
            UserDataHolder.userData.accessToken
        )
    }

    override fun onContactClick(contact: Contact, transitionPairs: Array<Pair<View, String>>) {
        val extras = FragmentNavigatorExtras(*transitionPairs)
        val direction =
            AddContactsFragmentDirections.actionAddContactsFragmentToContactProfile(
                !viewModel.supportList.contains(contact), contact
            )
        navController.navigate(direction, extras)
    }
}