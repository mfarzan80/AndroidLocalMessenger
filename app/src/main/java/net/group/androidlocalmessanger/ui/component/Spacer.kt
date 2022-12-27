package net.group.androidlocalmessanger.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VSpacer(space: Dp = 15.dp) {
    Spacer(modifier = Modifier.height(space))
}

@Composable
fun HSpacer(space: Dp = 15.dp) {
    Spacer(modifier = Modifier.width(space))
}