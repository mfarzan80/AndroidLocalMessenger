package net.group.androidlocalmessanger.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.group.androidlocalmessanger.module.Group

@Composable
fun GroupIcon(size: Dp = 50.dp) {
    Card(
        modifier = Modifier.size(size),
        backgroundColor = MaterialTheme.colors.surface,
        shape = CircleShape,
        elevation = 0.dp
    ) {
        Icon(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            imageVector = Icons.Default.Group,
            contentDescription = "group icon"
        )
    }
}
