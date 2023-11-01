package com.rhorbachevskyi.viewpager.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import kotlinx.coroutines.delay

typealias UsersPageLoader = suspend (from: Int, to: Int) -> List<Contact>

class ContactsPagingSource(
    private val loader: UsersPageLoader,
    private val pageSize: Int,
) : PagingSource<Int, Contact>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        // get the index of page to be loaded (it may be NULL, in this case let's load the first page with index = 0)
        val pageIndex = params.key ?: 0
        return try {
            log("load $pageIndex ${params.loadSize}")
            val users = loader.invoke(pageIndex, params.loadSize)
            delay(500L)
            return LoadResult.Page(
                data = users,

                prevKey = if (pageIndex == 0) null else pageIndex - 1,

                nextKey = if (users.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            // failed to load users -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? =null
}
