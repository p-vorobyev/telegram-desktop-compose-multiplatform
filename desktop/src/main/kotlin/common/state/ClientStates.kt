package common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import scene.dto.ChatPreview

data class ClientStates (
    var chatPreviews: SnapshotStateList<ChatPreview>,

    var selectedChatPreview: MutableState<ChatPreview?> = mutableStateOf(null),

    var chatsMemberCount: MutableMap<Long, Long>
)