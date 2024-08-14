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
import chat.dto.Content
import common.Colors.messageCardColor
import common.composable.CommonSelectionContainer


@Composable
fun MessageTextCard(message: ChatMessage) {
    Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
        Column(modifier = Modifier.padding(4.dp)) {
            val messageText = message.textContent.text
            val textEntities: Collection<Content.TextEntity> = message.textContent.entities
            if (messageText.isNotBlank()) {
                CommonSelectionContainer {
                    if (textEntities.isEmpty()) {
                        Text(text = messageText, fontSize = 14.sp)
                    } else {
                        AnnotatedMessageText(messageText = messageText, textEntities = textEntities)
                    }
                }
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
