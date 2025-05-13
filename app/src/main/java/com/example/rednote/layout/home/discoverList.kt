package com.example.rednote.layout.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// 数据类示例
data class ListItem(val id: Int, val height: Dp)

// 示例用的随机颜色 (可以复用之前的函数)
fun Color.Companion.randomColor(): Color {
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color(red, green, blue)
}

@Composable
fun DiscoverList() {
    // 示例数据：创建一些高度随机的数据项
    val itemsList = remember {
        (1..100).map {
            ListItem(it, Random.nextInt(100, 300).dp) // 随机高度在 100dp 到 300dp 之间
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),// 每行显示 2 列
        horizontalArrangement = Arrangement.spacedBy(4.dp),// 水平间距
        verticalItemSpacing = 4.dp,// 垂直间距
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(items = itemsList, key = { it.id }) {
            GridItem(item = it)
        }

    }
}

// 网格项的 Composable 示例
@Composable
fun GridItem(item: ListItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth() // 让项填充其所在的列的宽度
            .height(item.height) // 使用数据中的随机高度
            .background(Color.randomColor()) // 随机背景色，方便观察
            .padding(8.dp),
        contentAlignment = Alignment.Center // 内容居中
    ) {
        Text(text = "Item ${item.id}", fontSize = 16.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverListPreview() {
    DiscoverList()
}