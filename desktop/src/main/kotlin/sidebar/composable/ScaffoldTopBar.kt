package sidebar.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import sidebar.dto.ChatPreview
import java.util.*
import java.util.stream.Collectors

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
                    var imageBitMap: ImageBitmap? = null
                    chatPreview.photo?.let {
                        val img: ByteArray = Base64.getDecoder().decode(it)
                        imageBitMap = Bitmap.makeFromImage(Image.makeFromEncoded(img)).asComposeImageBitmap()
                    }
                    val title = chatPreview.title.replace("\n", " ")

                    Card(modifier = Modifier.fillMaxWidth().height(85.dp).background(MaterialTheme.colors.surface)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Spacer(modifier = Modifier.width(10.dp))

                            if (imageBitMap != null) {
                                Image(
                                    bitmap = imageBitMap!!,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(60.dp).clip(CircleShape),
                                    contentDescription = "",
                                    alignment = Alignment.Center
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .graphicsLayer {
                                            clip = true
                                            shape = CircleShape
                                        }.background(blueColor)
                                ) {
                                    val iconText = if (title.isBlank()) "" else
                                        title.split(" ").stream().limit(2).map { it.substring(0,1).uppercase() }.collect(
                                            Collectors.joining())
                                    Text(
                                        iconText,
                                        style = TextStyle(color = Color.White, fontSize = 20.sp),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            Column(verticalArrangement = Arrangement.Center) {
                                Text(fontWeight = FontWeight.Bold, text = if (title.length > 30) "${title.substring(0, 20)}..." else title)
                                Text("todo...")
                            }

                        }
                    }

                }
            }

        }
    }
}