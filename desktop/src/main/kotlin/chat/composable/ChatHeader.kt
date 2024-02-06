package chat.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import common.composable.ChatIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import common.state.ClientStates
import scene.dto.ChatType

@Composable
fun ChatHeader(clientStates: ClientStates) {

    var memberCount by remember { mutableStateOf(-1L) }

    val chatHeaderScope = rememberCoroutineScope()

    clientStates.selectedChatPreview.value?.let { chatPreview ->
        Card(modifier = Modifier.fillMaxWidth().height(85.dp).background(MaterialTheme.colors.surface)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                val cleanedTitle = chatPreview.title.replace("\n", " ")

                Spacer(modifier = Modifier.width(10.dp))

                ChatIcon(chatPreview.photo, cleanedTitle, circleSize = 60.dp)

                Spacer(modifier = Modifier.width(5.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Text(fontWeight = FontWeight.Bold, text = cleanedTitle)
                    if ((chatPreview.chatType == ChatType.BasicGroup || chatPreview.chatType == ChatType.Supergroup)) {
                        memberCount = clientStates.chatsMemberCount[chatPreview.id]!!
                        chatHeaderScope.launch {
                            while (true) {
                                memberCount = clientStates.chatsMemberCount[chatPreview.id]!!
                                delay(3000)
                            }
                        }
                        val membersInfo = if ( memberCount < 200)
                            "$memberCount members"
                        else
                            "$memberCount subscribers"
                        Text(membersInfo, fontSize = 12.sp)
                    }
                }

            }

        }
    }

}