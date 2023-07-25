package com.rhorbachevskyi.viewpager.presentation.ui.fragments.search

import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    private val databaseImpl: DatabaseImpl
) : ViewModel() {


    private val _currentList = MutableStateFlow<List<Contact>>(listOf())
    val currentList: StateFlow<List<Contact>> = _currentList

    private val startedListContact: ArrayList<Contact> = arrayListOf()

    fun initSearchList() = viewModelScope.launch(Dispatchers.IO) {
        _currentList.value = databaseImpl.getSearchList()
        startedListContact.clear()
        startedListContact.addAll(_currentList.value)
    }

    fun cancelSimpleNotification() {
        notificationManager.cancel(1)
    }

    fun updateContactList(newText: String?): Int {
        val filteredList = startedListContact.filter { contact: Contact ->
            contact.name?.contains(newText ?: "", ignoreCase = true) == true
        }
        _currentList.value = filteredList
        return filteredList.size
    }
}