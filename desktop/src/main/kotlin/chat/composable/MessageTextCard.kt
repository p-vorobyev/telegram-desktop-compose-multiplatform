package chat.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.dto.ChatMessage


@Composable
fun MessageTextCard(
    message: ChatMessage,
    messageCardColor: Color
) {
    Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
        Column(modifier = Modifier.padding(4.dp)) {
            if (message.messageText.isNotBlank()) {
                Text(text = message.messageText, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = if (message.editDate.isNotEmpty()) "edited ${message.editDate}" else message.date,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
