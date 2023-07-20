package com.rhorbachevskyi.viewpager.presentation.utils.ext

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupSwipeToDelete(
    deleteFunction: (position: Int) -> Unit,
    isSwipeEnabled: () -> Boolean
) {
    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            deleteFunction(viewHolder.bindingAdapterPosition)
        }

        override fun isItemViewSwipeEnabled(): Boolean = isSwipeEnabled()
    }
    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
}
