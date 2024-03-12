package lib.sketchbook.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.flowOf
import lib.sketchbook.util.LazyPagingItemsState.Type.*
import kotlin.Any

fun <T : Any> Iterable<T>.asPagingData(
    refresh: LoadState = LoadState.NotLoading(true),
    prepend: LoadState = LoadState.NotLoading(true),
    append: LoadState = LoadState.NotLoading(true)
) = PagingData.from(
    data = toList(),
    sourceLoadStates = LoadStates(refresh = refresh, prepend = prepend, append = append)
)

@Composable
fun <T : Any> ImmutableList<T>.rememberAsLazyPagingItems(
    refresh: LoadState = LoadState.NotLoading(true),
    prepend: LoadState = LoadState.NotLoading(true),
    append: LoadState = LoadState.NotLoading(true)
): LazyPagingItems<T> {
    val flow = remember(this, refresh, prepend, append) {
        flowOf(asPagingData(refresh, prepend, append))
    }
    return flow.collectAsLazyPagingItems()
}

@Composable
fun <T : Any> ImmutableList<T>.rememberAsLazyPagingItems(
    state: LoadState = LoadState.NotLoading(true)
) = rememberAsLazyPagingItems(
    refresh = state,
    prepend = state,
    append = state
)

@JvmInline
value class LazyPagingItemsState(private val items: LazyPagingItems<*>) {

    private inline val loadState get() = items.loadState
    private inline val append get() = loadState.append
    private inline val prepend get() = loadState.prepend
    private inline val refresh get() = loadState.refresh

    val isEmpty
        get() = items.itemCount <= 0

    val isNotEmpty
        inline get() = !isEmpty

    fun isLoading(type: Type = Any): Boolean = when (type) {
        Prepend -> prepend is LoadState.Loading
        Refresh -> refresh is LoadState.Loading
        Append -> append is LoadState.Loading
        All -> isLoading(Append) && isLoading(Prepend) && isLoading(Refresh)
        Any -> isLoading(Append) || isLoading(Prepend) || isLoading(Refresh)
    }

    fun isError(type: Type = Any): Boolean = when (type) {
        Prepend -> prepend is LoadState.Error
        Refresh -> refresh is LoadState.Error
        Append -> append is LoadState.Error
        All -> isError(Append) && isError(Prepend) && isError(Refresh)
        Any -> isError(Append) || isError(Prepend) || isError(Refresh)
    }

    fun isIdle(type: Type = Any): Boolean = when (type) {
        Prepend -> prepend is LoadState.NotLoading
        Refresh -> refresh is LoadState.NotLoading
        Append -> append is LoadState.NotLoading
        All -> isIdle(Append) && isIdle(Prepend) && isIdle(Refresh)
        Any -> isIdle(Append) || isIdle(Prepend) || isIdle(Refresh)
    }

    fun isEndReached(type: Type = Any): Boolean = when (type) {
        Prepend -> prepend.endOfPaginationReached
        Refresh -> refresh.endOfPaginationReached
        Append -> append.endOfPaginationReached
        All -> isEndReached(Append) && isEndReached(Prepend) && isEndReached(Refresh)
        Any -> isEndReached(Append) || isEndReached(Prepend) || isEndReached(Refresh)
    }

    enum class Type {
        Prepend, Refresh, Append, All, Any
    }

    companion object {
        fun LazyPagingItems<*>.state() = LazyPagingItemsState(this)
    }

}