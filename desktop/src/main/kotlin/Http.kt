import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

val mapper = jacksonObjectMapper()

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson ()
    }
    engine {
        requestTimeout = 90000
        https {
            trustManager = TrustAllManager()
        }
    }
}

const val baseUrl = "http://localhost:8080"

const val clientUri = "client"

const val authorizationUri = "authorization"

class TrustAllManager: X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        /*skip check*/
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        /*skip check*/
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray();
    }

}