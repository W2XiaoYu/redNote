package com.example.rednote.layout.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rednote.R
import com.example.rednote.widget.tabs.ScrollableTabRow
import kotlinx.coroutines.launch

@Composable
fun HomePageScreen() {
    var selectedIndex by remember { mutableStateOf(1) }
    val tabs = stringArrayResource(id = R.array.home_tabs).toList();
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {
        val selectedIndex by remember { derivedStateOf { pagerState.targetPage } }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.statusBars.asPaddingValues()
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center


        ) {
            ScrollableTabRow(
                modifier = Modifier
                    .background(Color.White)
                    .padding(
                        bottom = 6.dp,
                        top = 10.dp,
                    ),
                tabs = tabs.mapIndexed { index, label ->
                    {
                        Text(
                            text = label,
                            fontSize = 16.sp,
                            color = if (index == selectedIndex) Color.Black else Color.Gray,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                },
                tabHorizontalPadding = 14.dp,
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)

                    }
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 0.2.dp,
            color = Color.Gray.copy(alpha = 0.2f)
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .overscroll(overscrollEffect = null)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen("关注")
                1 -> DiscoverySession(
                    topPagerState = pagerState,
                    onScrollNextPage = {
                        scope.launch {
                            val target = pagerState.currentPage + it
                            if (target in 0 until pagerState.pageCount) {
                                pagerState.animateScrollToPage(target)
                            }
                        }
                    }
                )//内部还有一个tabs的界面
                2 -> HomeScreen("同城")
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
