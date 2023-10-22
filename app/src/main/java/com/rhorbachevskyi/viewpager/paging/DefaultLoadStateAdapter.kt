package com.rhorbachevskyi.viewpager.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rhorbachevskyi.viewpager.databinding.LoadMoreBinding

/**
 * Action to be executed when Try Again button is pressed
 */
typealias TryAgainAction = () -> Unit

/**
 * This adapter is used for rendering the load state (ProgressBar, error message and Try Again button)
 * in the list's header and footer.
 */
class DefaultLoadStateAdapter(
    private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoadMoreBinding.inflate(inflater, parent, false)
        return Holder(binding, null, tryAgainAction)
    }

    /**
     * The same layout is used for:
     * - footer
     * - main indicator
     */
    class Holder(
        private val binding: LoadMoreBinding,
        private val swipeRefreshLayout: SwipeRefreshLayout?,
        private val tryAgainAction: TryAgainAction
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnLoadMoreRetry.setOnClickListener { tryAgainAction() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            tvLoadMore.isVisible = loadState is LoadState.Error
            btnLoadMoreRetry.isVisible = loadState is LoadState.Error
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                prgBarLoadMore.isVisible = false
            } else {
                prgBarLoadMore.isVisible = loadState is LoadState.Loading
            }
        }
    }

}