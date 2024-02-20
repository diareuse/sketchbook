package lib.sketchbook

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter.State
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import kotlinx.coroutines.launch
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor
import lib.sketchbook.util.getLongArray
import lib.sketchbook.util.putLongArray
import lib.sketchbook.util.rememberSharedPrefs
import androidx.palette.graphics.Palette as AndroidPalette

@Composable
fun Image(
    data: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    loading: @Composable SubcomposeAsyncImageScope.(State.Loading) -> Unit = {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.requiredSize(24.dp),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
        }
    },
    success: @Composable (SubcomposeAsyncImageScope.(State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1f,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.None
) = Image(
    state = rememberImageState(data),
    modifier = modifier,
    contentDescription = contentDescription,
    loading = loading,
    success = success,
    error = error,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality
)

@Composable
fun Image(
    state: ImageState,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    loading: @Composable SubcomposeAsyncImageScope.(State.Loading) -> Unit = {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.requiredSize(24.dp),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
        }
    },
    success: @Composable (SubcomposeAsyncImageScope.(State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1f,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.None
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loader = remember(context) { state.getLoader(context) }
    val request = if (state.url != null) rememberImageRequest(state.url) else null
    SubcomposeAsyncImage(
        modifier = modifier,
        model = request,
        contentDescription = contentDescription,
        imageLoader = loader,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = colorFilter,
        onLoading = { scope.launch { state.processState(it) } },
        onSuccess = { scope.launch { state.processState(it) } },
        onError = { scope.launch { state.processState(it) } },
        loading = loading,
        error = error,
        success = success,
        filterQuality = filterQuality,
        alpha = alpha
    )
}

@Composable
private fun rememberImageRequest(url: Any?): ImageRequest {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    return remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .diskCachePolicy(CachePolicy.ENABLED)
            .lifecycle(owner)
            .precision(Precision.INEXACT)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    }
}

fun Any?.hashString() = "%08x".format(hashCode())
private const val ImageRetentionStore = "image-retention-store"

@Composable
fun rememberImageState(url: Any?): ImageState {
    return remember(url) { HardwareImageState(url) }
}

@Composable
fun rememberPaletteImageState(
    url: Any?,
    color: Color = Theme.color.container.background,
    contentColor: Color = Theme.color.contentColorFor(color)
): PaletteImageState {
    val prefs = rememberSharedPrefs(name = ImageRetentionStore)
    val key = remember(url) { url.hashString() }
    val default = remember(color, contentColor) {
        longArrayOf(
            color.value.toLong(),
            contentColor.value.toLong(),
            contentColor.value.toLong()
        )
    }
    val imageState = remember(url, default) {
        val storedColors = prefs.getLongArray(key, default)
        val defaultPalette = PaletteImageState.Palette(storedColors)
        PaletteImageState(
            url = url,
            defaultPalette = defaultPalette,
            resolvePalette = url != null && storedColors.contentEquals(default)
        )
    }
    LaunchedEffect(imageState.palette) {
        if (imageState.hasNewPalette) prefs.edit(commit = true) {
            putLongArray(key, imageState.palette.toLongArray())
        }
    }
    return imageState
}

@Stable
sealed class ImageState {

    @Stable
    abstract val url: Any?

    open fun getLoader(context: Context) = context.imageLoader

    internal open suspend fun processState(state: State) = Unit

}

@Stable
data class HardwareImageState(
    @Stable
    override val url: Any?
) : ImageState()

@Stable
data class PaletteImageState(
    @Stable
    override val url: Any?,
    private val defaultPalette: Palette,
    private val resolvePalette: Boolean
) : ImageState() {

    val hasNewPalette by derivedStateOf { defaultPalette != palette }

    var palette by mutableStateOf(defaultPalette)
        private set

    override fun getLoader(context: Context) = super.getLoader(context).newBuilder()
        .allowHardware(!resolvePalette)
        .build()

    override suspend fun processState(state: State) {
        super.processState(state)
        if (state !is State.Success || !resolvePalette) return
        try {
            palette = AndroidPalette.from(state.result.drawable.toBitmap())
                .resizeBitmapArea(200)
                .generate()
                .let(::Palette)
        } catch (ignore: IllegalStateException) {
        }
    }

    private fun Palette(palette: AndroidPalette): Palette {
        val swatch = palette.vibrantSwatch ?: palette.mutedSwatch ?: palette.dominantSwatch
        return Palette(
            color = Color(0xff000000 + (swatch?.rgb ?: 0)),
            textColor = Color(0xff000000 + (swatch?.bodyTextColor ?: 0)),
            titleColor = Color(0xff000000 + (swatch?.titleTextColor ?: 0))
        )
    }

    @Immutable
    data class Palette(
        val color: Color,
        val textColor: Color,
        val titleColor: Color
    ) {

        fun toLongArray() = longArrayOf(
            color.value.toLong(),
            textColor.value.toLong(),
            titleColor.value.toLong()
        )

        constructor(packed: LongArray) : this(
            color = Color(packed[0].toULong()),
            textColor = Color(packed[1].toULong()),
            titleColor = Color(packed[2].toULong())
        )

    }

}