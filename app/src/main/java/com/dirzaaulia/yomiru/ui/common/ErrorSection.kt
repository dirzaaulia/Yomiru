package com.dirzaaulia.yomiru.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorSection(
    modifier: Modifier = Modifier,
    errorMessage: String,
    doRetry: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = { doRetry.invoke() }) {
            Text(text = "Retry")
        }
    }
}