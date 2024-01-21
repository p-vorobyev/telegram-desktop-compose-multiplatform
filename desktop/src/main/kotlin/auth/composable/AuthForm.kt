package auth.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import auth.api.sendCode
import auth.api.sendPass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scene.composable.blueColor

enum class AuthType {
    CODE, PASSWORD
}

@Composable
fun AuthForm(type: AuthType, authScope: CoroutineScope = rememberCoroutineScope()) {
    var credentials by remember { mutableStateOf("") }
    var credSent by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(if (type == AuthType.CODE) "Authorization code" else "2-Step verification password")
        OutlinedTextField(
            value = credentials,
            onValueChange = { credentials = it },
            label = { Text(if (type == AuthType.CODE) "Code" else "Password") },
            modifier = Modifier.width(200.dp).height(60.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = blueColor
            ),
            singleLine = true,
            visualTransformation = {
                TransformedText(
                    AnnotatedString("*".repeat(it.text.length)),
                    OffsetMapping.Identity
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(
            onClick = {
                authScope.launch {
                    if (type == AuthType.CODE) {
                        sendCode(credentials)
                    } else {
                        sendPass(credentials)
                    }
                }
                credSent = true
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = blueColor),
            enabled = (credentials.trim().isNotBlank()) && !credSent)
        {
            Text("Send")
        }
    }
}