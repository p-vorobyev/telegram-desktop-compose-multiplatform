package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun <T> blockingIO(block: suspend CoroutineScope.() -> T) = runBlocking(Dispatchers.IO, block)

suspend fun <T> io(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO) { block() }
