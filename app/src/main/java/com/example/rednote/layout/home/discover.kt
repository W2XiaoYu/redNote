package com.example.rednote.layout.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.rednote.layout.tabs.ScrollableTabRow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun DiscoverySession() {
    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = stringArrayResource(id = R.array.home_middle_tabs).toList();
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        if (selectedIndex != pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
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
            selectedIndex = selectedIndex,
            onTabSelected = { index ->
                selectedIndex = index
                // 点击标签时滚动到对应页面
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
            HomeScreen(content = tabs[it])
        }
    }
}

@Composable
private fun HomeScreen(content: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = content)
    }
}

@Preview
@Composable
fun DiscoverySessionPreview() {
    DiscoverySession()
}