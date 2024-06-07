import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    fun api(): String = runBlocking{
        val client = HttpClient(CIO)
        val response = client.get("http://192.168.10.103:8080")
        response.body()
    }
}