package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf

class DefaultLoadStateAdapter:LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {
    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =FragmentUsersBinding.inflate(inflater)
        return Holder(binding, null)
    }
    class Holder(
        private val binding: FragmentUsersBinding,
        private val swipeRefreshLayout: SwipeRefreshLayout?,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if(swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                binding.progressBar.invisible()
            } else {
                binding.progressBar.visibleIf(loadState is LoadState.Loading)
            }
        }
    }
}