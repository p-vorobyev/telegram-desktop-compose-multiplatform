package common.composable

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import common.Colors.blueColor
import common.Colors.surfaceColor

@Composable
fun CircleButton(imageVector: ImageVector, onClick: () -> Unit) {
    Button(
        modifier = Modifier.size(44.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = blueColor),
        shape = CircleShape,
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Circle button",
            tint = surfaceColor
        )
    }
}