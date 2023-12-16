package sidebar.composable

import androidx.compose.runtime.snapshots.SnapshotStateList
import sidebar.dto.ChatPreview

data class SidebarStates(
    var chatPreviews: SnapshotStateList<ChatPreview>
)