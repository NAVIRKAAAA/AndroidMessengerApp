package com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.databinding.LoadMoreBinding

class DefaultLoadStateAdapter : LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoadMoreBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        private val binding: LoadMoreBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) = with(binding) {
            prgBarLoadMore.isVisible = loadState is LoadState.Loading
        }
    }
}