package com.rhorbachevskyi.viewpager.presentation.ui.fragments.search

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.databinding.FragmentSearchBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.search.adapter.SearchAdapter
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private val adapter: SearchAdapter = SearchAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialRecyclerview()
        searchView()
        setListeners()
        setObserver()
    }

    private fun initialRecyclerview() {
        viewModel.initSearchList()
        viewModel.cancelSimpleNotification()
        with(binding) {
            recyclerViewContacts.layoutManager = LinearLayoutManager(context)
            recyclerViewContacts.adapter = adapter
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.currentList.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapter.submitList(it)
            }
        }
    }

    private fun setListeners() {
        binding.imageViewNavigationBack.setOnClickListener { navController.navigateUp() }
    }

    private fun searchView() {
        with(binding) {
            imageSearchView.setOnCloseListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                textViewContacts.visible()
                imageViewNavigationBack.visible()
                false
            }
            imageSearchView.setOnSearchClickListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                textViewContacts.invisible()
                imageViewNavigationBack.invisible()
            }
            imageSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    updateSearchView(newText.toString())
                    return false
                }
            })
        }
    }

    private fun updateSearchView(newText: String?) {
        if (newText?.isBlank() == true) initialRecyclerview()
        setContactsText(newText)
    }

    private fun setContactsText(newText: String?) {
        val isContactListEmpty = viewModel.currentList.value.isEmpty()
        val isTextEmpty = newText?.isEmpty() == true
        val isNoResult =
            !isTextEmpty && viewModel.updateContactList(newText) == 0 && isContactListEmpty

        with(binding) {
            if (isContactListEmpty && isTextEmpty) {
                textViewMoreContacts.visible()
                textViewNoResultFound.visible()
            } else {
                textViewMoreContacts.visibleIf(isNoResult)
                textViewNoResultFound.visibleIf(isNoResult)
            }
        }
    }
}