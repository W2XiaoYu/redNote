package com.example.rednote.layout.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rednote.R
import com.example.rednote.widget.tabs.ScrollableTabRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverySession(
    topPagerState: PagerState,
    onScrollNextPage: suspend (step: Int) -> Unit,
) {

    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = stringArrayResource(id = R.array.home_middle_tabs).toList();
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size },
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 状态管理优化：完全由 pagerState 驱动
        val selectedIndex by remember { derivedStateOf { pagerState.targetPage } }

        ScrollableTabRow(
            isShowIndicator = false,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color.White),
            tabs = tabs.mapIndexed { index, label ->
                {
                    Text(
                        text = label,
                        color = if (index == selectedIndex) Color.Black else Color.Gray,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                    )
                }
            },
            tabVerticalPadding = 8.dp,
            selectedIndex = pagerState.currentPage,
            onTabSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)

                }
            }
        )
        // 是否在首尾页面
        val isFirstPage = pagerState.currentPage == 0
        val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
        HorizontalPager(
            userScrollEnabled = when (pagerState.currentPage) {
                0 -> false
                pagerState.pageCount - 1 -> false
                else -> true
            },
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .overscroll(overscrollEffect = null)
                .draggable(
                    state = rememberDraggableState { onDetail ->
                        println("当前页：${pagerState.currentPage}")
                        println("tabsize：${tabs.size}")
                        scope.launch {
                            when (pagerState.currentPage) {
                                0 -> {
                                    if (onDetail > 30) {// 向右滑动 -->父级
                                        onScrollNextPage(-1)
                                    }
                                    if (onDetail < -30) {// 向左滑动 -->子级的正常切换
                                        pagerState.animateScrollToPage(1)
                                    }
                                }
                                pagerState.pageCount - 1 -> { // 末页面（关键修正：用总页数计算末页索引）
                                    if (onDetail > 30) { // 向右滑动（delta>0）
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1) // 子级切换到前一页（如末页4→3）
                                    } else if (onDetail < -30) { // 向左滑动（delta<0）
                                        onScrollNextPage(1) // 通知父级向右翻页（如父级是ViewPager，1表示下一页）
                                    }
                                }
                            }
                        }
                    },
                    orientation = Orientation.Horizontal,
                    enabled = true,

                    ),

            ) {

            if (it == 0) {
                DiscoverList()
            } else {
                HomeScreen(content = tabs[it])
            }


        }
    }
}

@Composable
private fun HomeScreen(content: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = content)
    }
}

@Preview
@Composable
fun DiscoverySessionPreview() {
    DiscoverySession(
        onScrollNextPage = {},
        topPagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { 2 }

        ),
    )
}