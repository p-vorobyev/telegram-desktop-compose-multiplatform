package scene.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.composable.ChatHeader
import common.composable.CommonSelectionContainer
import common.state.ClientStates


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScaffoldTopBar(
    clientStates: ClientStates,
    chatSearchInput: MutableState<String> = mutableStateOf(""),
    filterUnreadChats: MutableState<Boolean> = mutableStateOf(false)
) {

    if (clientStates.chatList.isNotEmpty()) {
        Row {
            val topHeaderModifier = sidebarWidthModifier.height(50.dp).background(MaterialTheme.colors.surface)

            Column {

                Spacer(modifier = sidebarWidthModifier.height(5.dp).background(MaterialTheme.colors.surface))

                Row (modifier = topHeaderModifier) {
                    // to enable text selection
                    CommonSelectionContainer {
                        OutlinedTextField(
                            value = chatSearchInput.value,
                            onValueChange = { chatSearchInput.value = it },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = blueColor
                            ),
                            textStyle = TextStyle(fontSize = 14.sp),
                            singleLine = true
                        )
                    }
                }

                Row(
                    modifier = sidebarWidthModifier.height(30.dp).background(MaterialTheme.colors.surface),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Divider(modifier = sidebarWidthModifier.height(2.dp), color = greyColor)

            }

            Divider(modifier = Modifier.height(50.dp).width(2.dp), color = greyColor)

            Column {
                clientStates.selectedChatPreview.value?.let {
                    ChatHeader(clientStates)
                }
            }

        }
    }
}
