package common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import chat.dto.ChatMessage
import scene.dto.ChatPreview

object States {

    var chatList: SnapshotStateList<ChatPreview> = SnapshotStateList()

    var chatHistory: SnapshotStateList<ChatMessage> = SnapshotStateList()

    var selectedChatPreview: MutableState<ChatPreview?> = mutableStateOf(null)

    var chatsMemberCount: MutableMap<Long, Long> = mutableMapOf()

}