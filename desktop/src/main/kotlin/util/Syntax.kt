package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun <T> blockingIO(block: suspend CoroutineScope.() -> T) = runBlocking(Dispatchers.IO, block)
