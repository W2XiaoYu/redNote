package com.example.rednote.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* 防抖点击事件 */
fun Modifier.noRippleClickableWithThrottle(
    throttleIntervalMs: Long = 500L,
    pressFeedbackDurationMs: Long = 150L,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    var isClickable by remember { mutableStateOf(true) }
    var isPressed by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "clickAlphaAnimation"
    )

    this
        .graphicsLayer {
            this.alpha = alpha
        }
        .clickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = {
                if (isClickable) {
                    isClickable = false
                    isPressed = true
                    onClick()

                    coroutineScope.launch {
                        delay(pressFeedbackDurationMs) // 灰色保留时间
                        isPressed = false

                        // 剩下的时间控制 throttle
                        delay(throttleIntervalMs - pressFeedbackDurationMs)
                        isClickable = true
                    }
                }
            }
        )
}