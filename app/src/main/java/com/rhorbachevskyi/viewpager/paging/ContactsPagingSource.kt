package com.rhorbachevskyi.viewpager.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log

typealias UsersPageLoader = suspend (pageIndex: Int, pageSize: Int) -> List<UserData>

class ContactsPagingSource(
    private val loader: UsersPageLoader
) : PagingSource<Int, UserData>() {
    override fun getRefreshKey(state: PagingState<Int, UserData>): Int? {
        return null
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserData> {
        log("start load()")
        return try {
            val currentPage = params.key ?: 0
            val response = loader.invoke(currentPage, params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {

            LoadResult.Error(e)
        }
    }
}