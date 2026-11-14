package lib.sketchbook.catalog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.sketchbook.PagerIndicatorStyle
import lib.sketchbook.SketchbookBadge
import lib.sketchbook.SketchbookBadgedBox
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookCircularProgress
import lib.sketchbook.SketchbookEmptyState
import lib.sketchbook.SketchbookLinearProgress
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookPagerIndicator
import lib.sketchbook.SketchbookPullToRefresh
import lib.sketchbook.SketchbookSkeleton
import lib.sketchbook.SketchbookSkeletonCircle
import lib.sketchbook.SketchbookSkeletonText
import lib.sketchbook.SketchbookSnackbarHost
import lib.sketchbook.SketchbookToastHost
import lib.sketchbook.SketchbookToastState
import lib.sketchbook.SketchbookTooltip

internal val FeedbackDemos = listOf(
    Demo(Group.Feedback, "Badge") {
        Showcase("Dot and counts") {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SketchbookBadge()
                SketchbookBadge(count = 3)
                SketchbookBadge(count = 150)
            }
        }
        Showcase("Anchored") {
            SketchbookBadgedBox(badge = { SketchbookBadge(count = 7) }) { Icon(Icons.Default.Star, null, Modifier.size(32.dp)) }
        }
    },
    Demo(Group.Feedback, "Progress") {
        val progress by rememberInfiniteTransition(label = "progress").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
            label = "progress"
        )
        Showcase("Determinate") {
            SketchbookLinearProgress({ progress }, Modifier.fillMaxWidth())
            SketchbookCircularProgress({ progress })
        }
        Showcase("Indeterminate") {
            SketchbookLinearProgress(Modifier.fillMaxWidth())
            SketchbookCircularProgress()
        }
    },
    Demo(Group.Feedback, "Skeleton") {
        Showcase("Shapes") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                SketchbookSkeletonCircle(48.dp)
                SketchbookSkeleton(Modifier.fillMaxWidth().height(48.dp))
            }
        }
        Showcase("Text") { SketchbookSkeletonText() }
        Showcase("Static") { SketchbookSkeleton(Modifier.fillMaxWidth().height(24.dp), animated = false) }
    },
    Demo(Group.Feedback, "Snackbar") {
        val host = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Showcase("Host") {
            SketchbookOutlinedButton({ scope.launch { host.showSnackbar("Message sent", actionLabel = "Undo", withDismissAction = true) } }) {
                Text("Show snackbar")
            }
            SketchbookSnackbarHost(host)
        }
    },
    Demo(Group.Feedback, "Toast") {
        val state = remember { SketchbookToastState() }
        val scope = rememberCoroutineScope()
        Showcase("Queued toast") {
            SketchbookOutlinedButton({ scope.launch { state.show("Saved") } }) { Text("Show toast") }
            Box(Modifier.fillMaxWidth().height(80.dp)) { SketchbookToastHost(state, Modifier.align(Alignment.BottomCenter)) }
        }
    },
    Demo(Group.Feedback, "Tooltip") {
        Showcase("Long press") {
            SketchbookTooltip("Tooltip text") { SketchbookButton({}) { Text("Long press me") } }
        }
        Showcase("Custom content") {
            SketchbookTooltip(tooltip = { Text("Rich tooltip", Modifier.padding(8.dp)) }) {
                SketchbookOutlinedButton({}) { Text("Long press me too") }
            }
        }
    },
    Demo(Group.Feedback, "Empty state") {
        Showcase("Full") {
            SketchbookEmptyState(
                title = "No results found",
                message = "Try a different search term.",
                icon = { Icon(Icons.Default.Search, null, Modifier.size(48.dp)) },
                action = { SketchbookButton({}) { Text("Clear filters") } },
                modifier = Modifier.height(280.dp)
            )
        }
    },
    Demo(Group.Feedback, "Pull to refresh") {
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(2000)
                refreshing = false
            }
        }
        Showcase("Pull down inside the box") {
            SketchbookPullToRefresh(refreshing, { refreshing = true }, Modifier.fillMaxWidth().height(240.dp).testTag("pull-refresh-demo")) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(8) { Text("Row ${it + 1}") }
                }
            }
        }
    },
    Demo(Group.Feedback, "Pager indicator") {
        val pager = rememberPagerState { 5 }
        Showcase("Swipe the pager") {
            SketchbookCard(Modifier.fillMaxWidth().height(96.dp)) {
                HorizontalPager(pager) { page -> Text("Page ${page + 1}", Modifier.padding(32.dp)) }
            }
            SketchbookPagerIndicator(pager)
            SketchbookPagerIndicator(pager, style = PagerIndicatorStyle.Worm)
        }
        Showcase("Plain values") { SketchbookPagerIndicator(pageCount = 4, currentPage = 2) }
    }
)
