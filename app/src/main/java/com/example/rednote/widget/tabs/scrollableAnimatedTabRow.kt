package com.example.rednote.widget.tabs

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
import androidx.compose.ui.platform.LocalConfiguration
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
    tabHorizontalPadding: Dp = 8.dp,
    tabVerticalPadding: Dp = 0.dp,
    onTabSelected: (Int) -> Unit,
    indicatorColor: Color = Color.Red,
    indicatorHeight: Dp = 2.dp,
    isShowIndicator: Boolean = true,
    indicatorSpacing: Dp = 2.dp // 新增指示器与文字间距参数
) {
    val density = LocalDensity.current
    val tabWidths = remember { mutableStateListOf<Pair<Int, Int>>() }
    val indicatorOffsetX = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }
    val scrollState = rememberScrollState()
    val screenWidthPx =
        with(density) { LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp.toPx() } }
    val scope = rememberCoroutineScope() // <--- 在这里获取 scope

    // 封装滚动 + 动画逻辑
    fun scrollToTab(index: Int, animate: Boolean = true) {
        if (tabWidths.size > index) {
            val (tabWidth, tabHeight) = tabWidths[index]
            val paddingPx = with(density) { tabHorizontalPadding.roundToPx() }
            // 计算总偏移：所有前序tab宽度 + 左右padding
            val offset = tabWidths.take(index).sumOf { it.first + 2 * paddingPx }

            scope.launch {
                coroutineScope {
                    // 同步执行指示器动画
                    launch {
                        if (animate) {
                            indicatorOffsetX.animateTo(offset.toFloat(), tween(250))
                            indicatorWidth.animateTo(tabWidth.toFloat(), tween(250))
                        } else {
                            indicatorOffsetX.snapTo(offset.toFloat())
                            indicatorWidth.snapTo(tabWidth.toFloat())
                        }
                    }
                    // 同步执行滚动动画
                    launch {
                        val centerPos = offset + tabWidth / 2
                        val targetScroll = (centerPos - screenWidthPx / 2)
                            .coerceIn(0f, scrollState.maxValue.toFloat())

                        if (animate) {
                            scrollState.animateScrollTo(targetScroll.toInt())
                        } else {
                            scrollState.scrollTo(targetScroll.toInt())
                        }
                    }
                }
            }
        }
    }
    // 初始 or 外部变化时同步滚动
    LaunchedEffect(selectedIndex, tabWidths.size) {
        scrollToTab(selectedIndex, animate = true)
    }
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
                        .padding(horizontal = tabHorizontalPadding, vertical = tabVerticalPadding)
                        .onGloballyPositioned {
                            tabWidths.apply {
                                if (size <= index) add(it.size.width to it.size.height)
                                else set(index, it.size.width to it.size.height)
                            }
//                            val width = it.size.width
//                            if (tabWidths.size <= index) {
//                                tabWidths.add(width)
//                            } else {
//                                tabWidths[index] = width
//                            }
                        }
                        .noRippleClickableWithThrottle {
                            // 👇 先滚动，再更新选中状态，避免 UI 闪烁
//                            scrollToTab(index, animate = true)
                            onTabSelected(index)
                        }
                ) {
                    tabContent()
                }
            }

        }
        if (isShowIndicator && tabWidths.isNotEmpty()) {
            // 动态计算垂直位置：文字高度 + 间距
            val verticalOffset = with(density) {
                (tabWidths[selectedIndex].second + indicatorSpacing.toPx()).toInt()
            }
            Box(
                modifier = Modifier
                    .offset {
                        println(indicatorOffsetX.value.toInt())
                        IntOffset(
                            (indicatorOffsetX.value.toInt() + with(density) { tabHorizontalPadding.roundToPx() } - scrollState.value).toInt(),
                            y = verticalOffset

                        )
                    } // 加 padding 修正
                    .width(with(density) { indicatorWidth.value.toDp() })
                    .height(indicatorHeight)
                    .background(indicatorColor) // 可替换为你的 indicatorColor
            )
        }

    }

}