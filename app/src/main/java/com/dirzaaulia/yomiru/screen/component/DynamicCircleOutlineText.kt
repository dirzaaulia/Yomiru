package com.dirzaaulia.yomiru.screen.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DynamicCircleOutlineText(
    text: String,
    textSyle: TextStyle,
    borderColor: Color = Color.White,
    textColor: Color = Color.White,
    borderWidth: Dp = 1.dp,
    padding: Dp = 4.dp, // Padding between text and circle
) {
    SubcomposeLayout { constraints ->
        val paddingPx = padding.roundToPx()

        // First: measure the text
        val textPlaceable = subcompose("Text") {
            Text(
                text = text,
                style = textSyle,
                color = textColor,
            )
        }[0].measure(Constraints())

        // Calculate the required size for a perfect circle
        val diameter = maxOf(textPlaceable.width, textPlaceable.height) + paddingPx * 2
        val finalConstraints = Constraints.fixed(diameter, diameter)

        // Now measure the final layout inside the circle
        val finalPlaceable = subcompose("FinalText") {
            Box(
                modifier = Modifier
                    .size(with(LocalDensity.current) { diameter.toDp() })
                    .border(borderWidth, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = textSyle,
                    color = textColor,
                )
            }
        }[0].measure(finalConstraints)

        layout(diameter, diameter) {
            finalPlaceable.place(0, 0)
        }
    }
}