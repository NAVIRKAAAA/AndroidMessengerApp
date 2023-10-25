package com.rhorbachevskyi.viewpager.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.presentation.utils.Constants

typealias UsersPageLoader = suspend (from: Int, to: Int) -> List<UserData>

class ContactsPagingSource(
    private val loader: UsersPageLoader,
    private val pageSize: Int,
    private val allUsers: List<UserData>
) : PagingSource<Int, UserData>() {
    private var from: Int = 0
    private var to: Int = Constants.PAGINATION_LIST_RANGE
    override fun getRefreshKey(state: PagingState<Int, UserData>): Int? = null


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserData> {
        return try {
            val pageNumber = params.key ?: 0

            val response = loader.invoke(from, to)

            from = to
            to += Constants.PAGINATION_LIST_RANGE

            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (pageNumber + 1 == pageSize || to - Constants.PAGINATION_LIST_RANGE > allUsers.size) null else pageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}