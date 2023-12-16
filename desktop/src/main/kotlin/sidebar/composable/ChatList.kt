package sidebar.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sidebar.api.handleSidebarUpdates
import sidebar.dto.ChatPreview
import terminatingApp


@Composable
fun ChatList(sidebarStates: SidebarStates) {

    val selectedIndex: MutableState<Int> = remember { mutableStateOf(-1) }

    var needToScrollUpSidebar by remember { mutableStateOf(false) }

    val chatSearchInput: MutableState<String> = remember { mutableStateOf("") }

    val chatListUpdateScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val filterUnreadChats: MutableState<Boolean> = remember { mutableStateOf(false) }

    chatListUpdateScope.launch {
        while (!terminatingApp.get()) {
            val chatsSizeBeforeUpdates = sidebarStates.chatPreviews.size
            var firstChatPreviewBeforeUpdates: ChatPreview? = null
            if (sidebarStates.chatPreviews.isNotEmpty()) {
                firstChatPreviewBeforeUpdates = sidebarStates.chatPreviews[0]
            }

            handleSidebarUpdates(sidebarStates.chatPreviews)

            needToScrollUpSidebar = (sidebarStates.chatPreviews.size > chatsSizeBeforeUpdates) ||
                    (sidebarStates.chatPreviews.isNotEmpty() && lazyListState.firstVisibleItemIndex < 3 && firstChatPreviewBeforeUpdates != sidebarStates.chatPreviews[0])
            if (needToScrollUpSidebar) {
                lazyListState.scrollToItem(0)
            }

            delay(1000)
        }
    }

    Scaffold(
        topBar = { ScaffoldTopBar(sidebarStates, lazyListState, chatSearchInput, filterUnreadChats) },
        backgroundColor = greyColor
    ) {

        Row {

            LazyColumn(state = lazyListState, modifier = Modifier.width(450.dp).background(MaterialTheme.colors.surface).fillMaxHeight()) {

                itemsIndexed(sidebarStates.chatPreviews, {_, v -> v}) { index, chatPreview ->

                    if (chatSearchInput.value.isBlank() || chatPreview.title.contains(chatSearchInput.value, ignoreCase = true)) {
                        var hasUnread = false
                        chatPreview.unreadCount?.let {
                            if (it != 0) {
                                hasUnread = true
                            }
                        }
                        if (filterUnreadChats.value && hasUnread) {
                            ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedIndex = selectedIndex, index = index)
                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        } else if (!filterUnreadChats.value) {
                            ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedIndex = selectedIndex, index = index)
                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        }

                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedIndex.value == -1 && sidebarStates.chatPreviews.isNotEmpty()) {
                    Text("Chat not selected")
                } else if (selectedIndex.value != -1) {
                    Text(sidebarStates.chatPreviews[selectedIndex.value].title)
                }
            }

        }

    }

}