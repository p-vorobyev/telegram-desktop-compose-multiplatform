package sidebar.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import common.composable.ChatIcon
import kotlinx.coroutines.launch
import sidebar.dto.ChatPreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScaffoldTopBar(
    sidebarStates: SidebarStates,
    lazyListState: LazyListState = rememberLazyListState(),
    chatSearchInput: MutableState<String> = mutableStateOf(""),
    filterUnreadChats: MutableState<Boolean> = mutableStateOf(false),
    selectedIndex: MutableState<Int> = remember { mutableStateOf(-1) }
) {

    val chatListTopBarScope = rememberCoroutineScope()

    if (sidebarStates.chatPreviews.isNotEmpty()) {
        Row {
            val topHeaderModifier = Modifier.width(width = 450.dp).height(50.dp).background(MaterialTheme.colors.surface)

            Column {

                Spacer(modifier = Modifier.height(5.dp).width(450.dp).background(MaterialTheme.colors.surface))

                Row (modifier = topHeaderModifier) {
                    OutlinedTextField(
                        value = chatSearchInput.value,
                        onValueChange = { chatSearchInput.value = it },
                        placeholder = { Text("Search") },
                        modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = blueColor
                        ),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.width(width = 450.dp).height(30.dp).background(MaterialTheme.colors.surface),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (lazyListState.firstVisibleItemIndex > 3) {

                        Icon(
                            Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "Up",
                            modifier = Modifier.align(Alignment.CenterVertically)
                                .background(greyColor)
                                .onClick {
                                    chatListTopBarScope.launch {
                                        lazyListState.scrollToItem(0)
                                    }
                                }
                        )

                        Spacer(modifier = Modifier.width(50.dp))

                    }
                    var textDecorationAll by remember { mutableStateOf(TextDecoration.Underline) }
                    var textDecorationUnread by remember { mutableStateOf(TextDecoration.None) }

                    Text("All", textDecoration = textDecorationAll, modifier = Modifier.onClick {
                        textDecorationAll = TextDecoration.Underline
                        textDecorationUnread = TextDecoration.None
                        filterUnreadChats.value = false
                    })

                    Spacer(modifier = Modifier.width(50.dp))

                    Text("Unread", textDecoration = textDecorationUnread, modifier = Modifier.onClick {
                        textDecorationAll = TextDecoration.None
                        textDecorationUnread = TextDecoration.Underline
                        filterUnreadChats.value = true
                    })

                }

                Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)

            }

            Column {
                if (selectedIndex.value != -1) {

                    val chatPreview: ChatPreview = sidebarStates.chatPreviews[selectedIndex.value]

                    val cleanedTitle = chatPreview.title.replace("\n", " ")

                    Card(modifier = Modifier.fillMaxWidth().height(85.dp).background(MaterialTheme.colors.surface)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Spacer(modifier = Modifier.width(10.dp))

                            ChatIcon(chatPreview.photo, cleanedTitle)

                            Spacer(modifier = Modifier.width(5.dp))

                            Column(verticalArrangement = Arrangement.Center) {
                                Text(fontWeight = FontWeight.Bold, text = if (cleanedTitle.length > 30) "${cleanedTitle.substring(0, 20)}..." else cleanedTitle)
                                Text("todo...")
                            }

                        }
                    }

                }
            }

        }
    }
}