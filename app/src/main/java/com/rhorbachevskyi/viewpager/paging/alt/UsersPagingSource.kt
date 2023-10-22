package com.rhorbachevskyi.viewpager.paging.alt

//class UsersPagingSource(
//    private val loader: UsersPageLoader,
//    private val pageSize: Int,
//) : PagingSource<Int, Contact>() {
//    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
//        val anchorPosition = state.anchorPosition ?: return null
//        val page = state.closestPageToPosition(anchorPosition) ?: return null
//        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
//        val pageIndex = params.key ?: 0
//        return try {
//            val users = loader.invoke(pageIndex, params.loadSize)
//            return LoadResult.Page(
//                data = users,
//                prevKey = if (pageIndex == 0) null else pageIndex - 1,
//                nextKey = if (users.size == params.loadSize) pageIndex + (params.loadSize * pageSize) else null
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(throwable = e)
//        }
//    }
//}