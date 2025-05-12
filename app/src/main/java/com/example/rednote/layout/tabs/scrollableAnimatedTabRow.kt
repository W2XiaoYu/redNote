package com.example.rednote.layout.tabs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.rednote.utils.noRippleClickableWithThrottle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableTabRow(
    modifier: Modifier = Modifier,
    tabs: List<@Composable () -> Unit>,
    selectedIndex: Int,
    tabPadding: Dp = 8.dp,
    onTabSelected: (Int) -> Unit,
    indicatorColor: Color = Color.Red,
    indicatorHeight: Dp = 2.dp,
) {
    val density = LocalDensity.current
    val tabWidths = remember { mutableStateListOf<Int>() }
    val indicatorOffsetX = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }
    val scrollState = rememberScrollState()
    val screenWidthPx =
        with(density) { LocalDensity.current.run { androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp.toPx() } }
    val scope = rememberCoroutineScope() // <--- 在这里获取 scope

    // 封装滚动 + 动画逻辑
    fun scrollToTab(index: Int, animate: Boolean = true) {
        if (tabWidths.size > index) {
            val paddingPx = with(density) { tabPadding.roundToPx() }
            val offset = tabWidths.take(index).sum() + paddingPx * index * 2
            val width = tabWidths[index]
            val centerPosition = offset + width / 2
            val targetScroll = (centerPosition - screenWidthPx / 2).toInt().coerceAtLeast(0)

            scope.launch {
                if (animate) {
                    indicatorOffsetX.animateTo(offset.toFloat(), tween(250))
                    indicatorWidth.animateTo(width.toFloat(), tween(250))
                    scrollState.animateScrollTo(targetScroll)
                } else {
                    indicatorOffsetX.snapTo(offset.toFloat())
                    indicatorWidth.snapTo(width.toFloat())
                    scrollState.scrollTo(targetScroll)
                }
            }
        }
    }
    // 初始 or 外部变化时同步滚动
    LaunchedEffect(selectedIndex, tabWidths.size) {
        scrollToTab(selectedIndex, animate = true)
    }
//    LaunchedEffect(selectedIndex, tabWidths) {
//        if (tabWidths.size > selectedIndex) {
//            val paddingPx = with(density) { tabPadding.roundToPx() }
//            // 1. 正确的 indicatorOffset：不管 scroll 了多少，indicator 是相对 Row 的 layout 的
//            val offset = tabWidths.take(selectedIndex).sum() + paddingPx * selectedIndex * 2
//            val width = tabWidths[selectedIndex]
//
//            indicatorOffsetX.animateTo(offset.toFloat(), tween(250))
//            indicatorWidth.animateTo(width.toFloat(), tween(250))
//            // 计算应该滚动到的位置：选中 tab 中心 - 屏幕一半
//            val centerPosition = offset + width / 2
//            val targetScroll = (centerPosition - screenWidthPx / 2).toInt().coerceAtLeast(0)
//
//            scrollState.animateScrollTo(targetScroll)
//        }
//    }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)

        ) {
            tabs.forEachIndexed { index, tabContent ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = tabPadding, vertical = 12.dp)
                        .onGloballyPositioned {
                            val width = it.size.width
                            if (tabWidths.size <= index) {
                                tabWidths.add(width)
                            } else {
                                tabWidths[index] = width
                            }
                        }
                        .noRippleClickableWithThrottle {
                            onTabSelected(index)
                            scrollToTab(index, animate = true)
                        }
                ) {
                    tabContent()
                }
            }

        }
        Box(
            modifier = Modifier
                .offset {
                    println(indicatorOffsetX.value.toInt())
                    IntOffset(
                        (indicatorOffsetX.value.toInt() + with(density) { tabPadding.roundToPx() } - scrollState.value).toInt(),
                        0

                    )
                } // 加 padding 修正
                .padding(top = 42.dp)
                .width(with(density) { indicatorWidth.value.toDp() })
                .height(indicatorHeight)
                .background(indicatorColor) // 可替换为你的 indicatorColor
        )
    }

}