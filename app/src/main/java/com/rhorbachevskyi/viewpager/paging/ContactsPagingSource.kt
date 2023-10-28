package com.rhorbachevskyi.viewpager.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rhorbachevskyi.viewpager.data.model.Contact

typealias UsersPageLoader = suspend (from: Int, to: Int) -> List<Contact>

class ContactsPagingSource(
    private val loader: UsersPageLoader,
    private val pageSize: Int,
) : PagingSource<Int, Contact>() {
    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        return try {
            val pageIndex = params.key ?: 0
            val response = loader.invoke(pageIndex, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
