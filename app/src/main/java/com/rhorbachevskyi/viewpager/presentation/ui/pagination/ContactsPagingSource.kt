package com.rhorbachevskyi.viewpager.presentation.ui.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rhorbachevskyi.viewpager.data.model.Contact
import kotlinx.coroutines.delay

typealias UsersPageLoader = suspend (from: Int, to: Int) -> List<Contact>

class ContactsPagingSource(
    private val loader: UsersPageLoader,
    private val pageSize: Int,
) : PagingSource<Int, Contact>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        val pageIndex = params.key ?: 0
        return try {
            val users = loader.invoke(pageIndex, params.loadSize)
            delay(500L)
            return LoadResult.Page(
                data = users,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (users.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? =null
}
