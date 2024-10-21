package com.sf.contactmanager.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sf.contactmanager.model.Contact

class ContactPagingSource(private val data:List<Contact>):PagingSource<Int,Contact>() {
    private val pageSize = 20

    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
        return state.anchorPosition?.let {
            val page = state.closestPageToPosition(it)
            return page?.nextKey?.minus(1) ?:page?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        val page = params.key?:0
        val begin = page*pageSize
        val end = minOf(begin+pageSize,data.size)
        val result = data.subList(begin,end)
        return LoadResult.Page(
            result,
            prevKey = if (page>0) page-1 else null,
            nextKey = if (end<data.size) page+1 else null
        )

    }
}