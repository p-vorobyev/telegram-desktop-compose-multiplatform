package common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import chat.dto.ChatMessage
import scene.dto.ChatPreview

object ClientStates {

    var chatList: SnapshotStateList<ChatPreview> = SnapshotStateList()

    var chatHistory: SnapshotStateList<ChatMessage> = SnapshotStateList()

    var selectedChatPreview: MutableState<ChatPreview?> = mutableStateOf(null)

    var chatsMemberCount: MutableMap<Long, Long> = mutableMapOf()

}