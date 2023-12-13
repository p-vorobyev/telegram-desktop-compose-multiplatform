package auth.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import transport.authorizationUri
import transport.baseUrl
import transport.httpClient

enum class Status {
    AUTHORIZED, NOT_AUTHORIZED
}

data class Credential(val value: String)

suspend fun authorizationStatus(): Status {
    val status = httpClient.get("${baseUrl}/${authorizationUri}/status").bodyAsText()
    return Status.valueOf(status)
}

suspend fun waitCode(): Boolean {
    val value = httpClient.get("${baseUrl}/${authorizationUri}/waitCode").bodyAsText()
    return value == "true"
}

suspend fun waitPass(): Boolean {
    val value = httpClient.get("${baseUrl}/${authorizationUri}/waitPass").bodyAsText()
    return value == "true"
}

suspend fun sendCode(code: String) {
    httpClient.post {
        url("${baseUrl}/${authorizationUri}/code")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(Credential(code));
    }
}

suspend fun sendPass(pass: String) {
    httpClient.post {
        url("${baseUrl}/${authorizationUri}/password")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(Credential(pass));
    }
}