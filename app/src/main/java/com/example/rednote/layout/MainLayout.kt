package com.example.rednote.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rednote.R
import com.example.rednote.layout.home.HomePageScreen
import com.example.rednote.utils.noRippleClickableWithThrottle


@Composable
fun MainLayout() {
    val bottomList = stringArrayResource(id = R.array.app_bottom_tabs).toList();
    val viewModel: MainLayoutViewModel = viewModel()
    val initIndex by viewModel.initIndex.collectAsState()
    var showDiscoveryDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (initIndex) {
                0 -> HomePageScreen()
                1 -> Text("同城")
                2 -> Text("发现")
                3 -> Text("我")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomList.forEachIndexed { index, text ->
                BottomItem(
                    text = text,
                    isSelected = initIndex == index,
                    onClick = {
                        if (index == 2) {
                            showDiscoveryDialog = true
                            return@BottomItem
                        }
                        viewModel.setInitIndex(index)
                    }
                )
            }

        }

    }
    if (showDiscoveryDialog) {
        AlertDialog(
            onDismissRequest = { showDiscoveryDialog = false },
            title = { Text("发现频道") },
            text = { Text("请选择您想查看的内容类型：") },
            confirmButton = {
                TextButton(onClick = { showDiscoveryDialog = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscoveryDialog = false }) {
                    Text("取消")
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .shadow(8.dp, shape = RoundedCornerShape(8.dp))
        )
    }
}


@Composable
fun BottomItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.Unspecified)
            .noRippleClickableWithThrottle() {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (text == "add") {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(30.dp)
                    .background(Color.Red, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    contentDescription = "添加",
                    painter = painterResource(id = R.drawable.icon_add), tint = Color.White,
                )
            }
        } else {
            Text(
                text = text,
                style = TextStyle(
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

    }
}


@Composable
@Preview(
    backgroundColor = 0xFFFFFF,
)
fun MainLayoutPreview(

) {
    MainLayout()
}